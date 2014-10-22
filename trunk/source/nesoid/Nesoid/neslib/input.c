/* FCE Ultra - NES/Famicom Emulator
 *
 * Copyright notice for this file:
 *  Copyright (C) 1998 BERO
 *  Copyright (C) 2002 Ben Parnell
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
#include <string.h>
#include "types.h"
#include "x6502.h"

#include "fce.h"
#include "sound.h"
#include "netplay.h"
#include "svga.h"

#include "input.h"
#include "state.h"
#include "movie.h"

#include "fds.h"
#include "vsuni.h"
#include "dprintf.h"

extern INPUTC *FCEU_InitZapper(int w);
extern INPUTC *FCEU_InitPowerpadA(int w);
extern INPUTC *FCEU_InitArkanoid(int w);

extern INPUTCFC *FCEU_InitArkanoidFC(void);
extern INPUTCFC *FCEU_InitSpaceShadow(void);
extern INPUTCFC *FCEU_InitFKB(void);
static uint8 joy_readbit[2];
static uint8 joy[4]={0,0,0,0};
static uint8 LastStrobe;

static int FSDisable=0;	/* Set to 1 if NES-style four-player adapter is disabled. */
static int JPAttrib[2]={0,0};
static int JPType[2]={0,0};
static void *InputDataPtr[2];

static int JPAttribFC=0;
static int JPTypeFC=0;
static void *InputDataPtrFC;

void (*InputScanlineHook)(uint8 *bg, uint8 *spr, uint32 linets, int final);

static INPUTC DummyJPort={0,0,0,0,0};
static INPUTC *JPorts[2]={&DummyJPort,&DummyJPort};
static INPUTCFC *FCExp=0;

/* This function is a quick hack to get the NSF player to use emulated gamepad
   input.
*/
uint8 FCEU_GetJoyJoy(void)
{
 return(joy[0]|joy[1]|joy[2]|joy[3]);
}

static uint8 FP_FASTAPASS(1) ReadGPVS(int w)
{
                uint8 ret=0;

		if(joy_readbit[w]>=8)
		 ret=1;
		else
		{
                 ret = ((joy[w]>>(joy_readbit[w]))&1);
                 joy_readbit[w]++;
		}
                return ret;
}

static uint8 FP_FASTAPASS(1) ReadGP(int w)
{
                uint8 ret;
                //if(JoyMulti)
		//{
                 //ret = ((joy[w]>>(joy_readbit[w]))&1)|
                 //(((joy[w]>>(joy_readbit[w]+8))&1)<<1);
                 //if(joy_readbit[w]>8) ret=0;
		//}
                ret = ((joy[w]>>(joy_readbit[w]))&1);
		if(FSDisable)
		{
	  	 if(joy_readbit[w]>=8) ret|=1;
		}
		else
		{
                 if(joy_readbit[w]==19-w) ret|=1;
		}
		joy_readbit[w]++;
                return ret;
}

static DECLFR(JPRead)
{
	uint8 ret=0;

	if(JPorts[A&1]->Read)
	 ret|=JPorts[A&1]->Read(A&1);

	if(FCExp)
	 if(FCExp->Read)
	  ret=FCExp->Read(A&1,ret);

	ret|=X.DB&0xC0;
	dprintf("JPRead %i %02x", A&1, ret);
	return(ret);
}

static DECLFW(B4016)
{
	if(FCExp)
	 if(FCExp->Write)
	  FCExp->Write(V&7);

	if(JPorts[0]->Write)
	 JPorts[0]->Write(V&1);
        if(JPorts[1]->Write)
         JPorts[1]->Write(V&1);

        if((LastStrobe&1) && (!(V&1)))
        {
	 /* This strobe code is just for convenience.  If it were
	    with the code in input / *.c, it would more accurately represent
	    what's really going on.  But who wants accuracy? ;)
	    Seriously, though, this shouldn't be a problem.
	 */
	 if(JPorts[0]->Strobe)
	  JPorts[0]->Strobe(0);
         if(JPorts[1]->Strobe)
          JPorts[1]->Strobe(1);
	 if(FCExp)
	  if(FCExp->Strobe)
	   FCExp->Strobe();
	 }
         LastStrobe=V&1;
}

static void FP_FASTAPASS(1) StrobeGP(int w)
{
	joy_readbit[w]=0;
}

static INPUTC GPC={ReadGP,0,StrobeGP,0,0,0};
static INPUTC GPCVS={ReadGPVS,0,StrobeGP,0,0,0};

void DrawInput(uint8 *buf)
{
 int x;

 for(x=0;x<2;x++)
  if(JPorts[x]->Draw)
   JPorts[x]->Draw(x,buf,JPAttrib[x]);
 if(FCExp)
  if(FCExp->Draw)
   FCExp->Draw(buf,JPAttribFC);
}

void UpdateInput(void)
{
	int x;

	for(x=0;x<2;x++)
	{
	 switch(JPType[x])
	 {
	  case SI_GAMEPAD:
		if(!x) joy[0]=*(uint16 *)InputDataPtr[0];
		else joy[1]=*(uint16 *)InputDataPtr[1];
		break;
	  default:
		if(JPorts[x]->Update)
		 JPorts[x]->Update(x,InputDataPtr[x],JPAttrib[x]);
		break;
	 }
	}
	if(FCExp)
	 if(FCExp->Update)
	  FCExp->Update(InputDataPtrFC,JPAttribFC);

        if(FCEUGameInfo.type==GIT_VSUNI)
        {
	 uint16 t=joy[0];
	 joy[0]=(joy[0]&0xC)|(joy[1]&0xF3);
	 joy[1]=(joy[1]&0xC)|(t&0xF3);
	 if(coinon) coinon--;
	}
	#ifdef NETWORK
	if(netplay) NetplayUpdate(&joy[0],&joy[1]);
	#endif
	if (current < 0) FCEUMOV_AddJoy(joy);
	else framecount++; // for debug
	//FlushCommandQueue();
}

static DECLFR(VSUNIRead0)
{
	uint8 ret=0;

        if(JPorts[0]->Read)
         ret|=(JPorts[0]->Read(0))&1;

	ret|=(vsdip&3)<<3;
	if(coinon)
	 ret|=0x4;
	return ret;
}

static DECLFR(VSUNIRead1)
{
        uint8 ret=0;

        if(JPorts[1]->Read)
         ret|=(JPorts[1]->Read(1))&1;
        ret|=vsdip&0xFC;
        return ret;
}

static void SLHLHook(uint8 *bg, uint8 *spr, uint32 linets, int final)
{
 int x;

 for(x=0;x<2;x++)
  if(JPorts[x]->SLHook)
   JPorts[x]->SLHook(x,bg,spr,linets,final);
 if(FCExp)
  if(FCExp->SLHook)
   FCExp->SLHook(bg,spr,linets,final);
}

static void CheckSLHook(void)
{
        InputScanlineHook=0;
        if(JPorts[0]->SLHook || JPorts[1]->SLHook)
         InputScanlineHook=SLHLHook;
        if(FCExp)
         if(FCExp->SLHook)
          InputScanlineHook=SLHLHook;
}

static void FASTAPASS(1) SetInputStuff(int x)
{
 	 switch(JPType[x])
	 {
	  case SI_GAMEPAD:
           if(FCEUGameInfo.type==GIT_VSUNI)
	    JPorts[x]=&GPCVS;
	   else
	    JPorts[x]=&GPC;
	  break;
	  case SI_ARKANOID:JPorts[x]=FCEU_InitArkanoid(x);break;
	  case SI_ZAPPER:JPorts[x]=FCEU_InitZapper(x);break;
          case SI_POWERPADA:JPorts[x]=FCEU_InitPowerpadA(x);break;
	  case SI_NONE:JPorts[x]=&DummyJPort;break;
         }

	CheckSLHook();
}

static uint8 F4ReadBit[2];
static void StrobeFami4(void)
{
 F4ReadBit[0]=F4ReadBit[1]=0;
}

static uint8 FP_FASTAPASS(2) ReadFami4(int w, uint8 ret)
{
 ret&=1;

 ret |= ((joy[w]>>(F4ReadBit[w]+8))&1)<<1;
 if(F4ReadBit[w]>=8) ret|=2;
 else F4ReadBit[w]++;

 return(ret);
}

static INPUTCFC FAMI4C={ReadFami4,0,StrobeFami4,0,0,0};
static void SetInputStuffFC(void)
{
        switch(JPTypeFC)
        {
         case SIFC_NONE:FCExp=0;break;
         case SIFC_ARKANOID:FCExp=FCEU_InitArkanoidFC();break;
	 case SIFC_SHADOW:FCExp=FCEU_InitSpaceShadow();break;
         case SIFC_4PLAYER:FCExp=&FAMI4C;memset(&F4ReadBit,0,sizeof(F4ReadBit));break;
	 case SIFC_FKB:FCExp=FCEU_InitFKB();break;
        }
	CheckSLHook();
}

// VS Unisystem code called after SetInputMap() hooks B4016.  Need to
// rewrite code to make this more sane?

void InitializeInput(void)
{
	memset(joy_readbit,0,sizeof(joy_readbit));
	memset(joy,0,sizeof(joy));
        LastStrobe=0;

        if(FCEUGameInfo.type==GIT_VSUNI)
        {
         SetReadHandler(0x4016,0x4016,VSUNIRead0);
         SetReadHandler(0x4017,0x4017,VSUNIRead1);
        }
        else
         SetReadHandler(0x4016,0x4017,JPRead);
        SetWriteHandler(0x4016,0x4016,B4016);

        SetInputStuff(0);
        SetInputStuff(1);
	SetInputStuffFC();
}

void FCEUI_SetInput(int port, int type, void *ptr, int attrib)
{
 JPAttrib[port]=attrib;
 JPType[port]=type;
 InputDataPtr[port]=ptr;
 SetInputStuff(port);
}

void FCEUI_DisableFourScore(int s)
{
 FSDisable=s;
}

void FCEUI_SetInputFC(int type, void *ptr, int attrib)
{
 JPAttribFC=attrib;
 JPTypeFC=type;
 InputDataPtrFC=ptr;
 SetInputStuffFC();
}

void FCEU_DoSimpleCommand(int cmd)
{
   printf("FCEU_DoSimpleCommand: %i\n", cmd);
 switch(cmd)
 {
   case FCEUNPCMD_FDSINSERT: FCEU_FDSInsert();break;
   case FCEUNPCMD_FDSSELECT: FCEU_FDSSelect();break;
//   case FCEUNPCMD_FDSEJECT: FCEU_FDSEject();break;
   case FCEUNPCMD_VSUNICOIN: FCEU_VSUniCoin(); break;
   case FCEUNPCMD_VSUNIDIP0 ... (FCEUNPCMD_VSUNIDIP0 + 7): FCEU_VSUniToggleDIP(cmd - FCEUNPCMD_VSUNIDIP0);break;
   case FCEUNPCMD_POWER: PowerNES();break;
   case FCEUNPCMD_RESET: ResetNES();break;
   default: printf("FCEU_DoSimpleCommand: can't handle cmd %i\n", cmd); break;
 }
}

void FCEU_QSimpleCommand(int cmd)
{
// if(FCEUnetplay)
//  FCEUNET_SendCommand(cmd, 0);
// else
 {
  // totally wrong:
//  if(!FCEUMOV_IsPlaying())
//   FCEU_DoSimpleCommand(cmd);
//  else
//   FCEUMOV_AddCommand(cmd);

  FCEU_DoSimpleCommand(cmd);
//  if(FCEUMOV_IsRecording())
//   FCEUMOV_AddCommand(cmd);
 }
}

void FCEUI_FDSSelect(void)
{
 FCEU_QSimpleCommand(FCEUNPCMD_FDSSELECT);
}

int FCEUI_FDSInsert(void)
{
 FCEU_QSimpleCommand(FCEUNPCMD_FDSINSERT);
 return(1);
}

/*
int FCEUI_FDSEject(void)
{
    FCEU_QSimpleCommand(FCEUNPCMD_FDSEJECT);
    return(1);
}
*/
void FCEUI_VSUniToggleDIP(int w)
{
 FCEU_QSimpleCommand(FCEUNPCMD_VSUNIDIP0 + w);
}

void FCEUI_VSUniCoin(void)
{
 FCEU_QSimpleCommand(FCEUNPCMD_VSUNICOIN);
}

void FCEUI_ResetNES(void)
{
	FCEU_QSimpleCommand(FCEUNPCMD_RESET);
}

void FCEUI_PowerNES(void)
{
        FCEU_QSimpleCommand(FCEUNPCMD_POWER);
}



SFORMAT FCEUCTRL_STATEINFO[]={
 { joy_readbit, 2, "JYRB"},
 { joy, 4, "JOYS"},
 { &LastStrobe, 1, "LSTS"},
 { 0 }
};


