/*
 *  changecode.c
 *  zip
 *
 *  Created by sie kensou on 10-6-19.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "changecode.h"
#include <string.h>
#include <iconv.h>
#include <stdio.h>

#define MIN_CHINESE_COUNT 12

static int isUTF8(const char* str)
{
	int i;
	unsigned char cOctets;
	unsigned char chr;
	int bAllAscii = 1;
	long iLen = strlen(str);
	
	cOctets = 0;
	for ( i=0; i<iLen; i++)
	{
		chr = (unsigned char)str[i];
		if ((chr & 0x80) != 0)
			bAllAscii = 0;
		if (cOctets == 0)
		{
			if (chr >= 0x80)
			{
				do
				{
					chr <<= 1;
					cOctets++;
				}
				while ((chr&0x80) != 0);
				cOctets--;
				if (cOctets == 0)
					return 0;
			}
		}
		else
		{
			if ((chr&0xC0) != 0x80)
				return 0;
			cOctets--;
		}
	}
	
	if (cOctets > 0)
		return 0;
	
	if (bAllAscii)
		return 0;
	
	return 1;
}

static int hasBig5Char(const unsigned char* buff)
{
	unsigned char* pszStr = (unsigned char*)buff;
	int nHi_A42A9Count = 0;
	int nHi_AA2AFCount = 0;
	int nHi_C62D7Count = 0;
	int nHi_C62C7Count = 0;
	int nHi_AA2AF_And_LoA12FECount = 0;
	
	while(pszStr && *(pszStr+1))
	{
		if(nHi_C62D7Count >= 5 || nHi_A42A9Count + nHi_AA2AFCount >= 5)
			break;
		if( (unsigned char)*pszStr >= 0x80 )//汉字
		{
			unsigned char hi_c = *pszStr;
			unsigned char lo_c = *(pszStr+1);
			if(hi_c >= 0xC6 && hi_c <= 0xD7)//简体常用字
			{
				nHi_C62D7Count++;
				if(hi_c <= 0xC7)
				{
					nHi_C62C7Count++;
				}
			}
			else if(hi_c >= 0xA4 && hi_c <= 0xAF)//繁体
			{
				if(hi_c <= 0xA9)
				{
					nHi_A42A9Count++;
				}
				else
				{
					if(lo_c >= 0xA1 && lo_c <= 0xFE)
					{
						nHi_AA2AF_And_LoA12FECount++;
						return 1;
					}
					nHi_AA2AFCount++;
				}
			}
			pszStr = pszStr + 2;
		}
		else
		{
			pszStr++;
		}
	}
	
	if(nHi_C62D7Count >= nHi_A42A9Count + nHi_AA2AFCount)
	{
		return 0;
	}
	return 1;
}

static char *getTextEncodeType(unsigned char* textData, int nLength)
{
	char *encodeType = "GBK";
	
	if ( textData == NULL || nLength <= 0 )
		return NULL;
	
	char *pJudge = (char*)textData;
	int GotEnCode = 1;
	
	
	//check whether it has an BOM identify
	if(nLength >= 3 && 0xEF == (unsigned char)pJudge[0] && 0xBB == (unsigned char)pJudge[1] && 0xBF == (unsigned char)pJudge[2])
	{
		encodeType = "UTF8";
	}
	else if( nLength >= 2 && 0xFE == (unsigned char)pJudge[0] && 0xFF == (unsigned char)pJudge[1])
	{
		encodeType = "UNICODEBIG";
	}
	else if(nLength >= 2 && 0xFF == (unsigned char)pJudge[0] && 0xFE == (unsigned char)pJudge[1])
	{
		encodeType = "UNICODELITTLE";
	}
	else
	{
		GotEnCode = 0;
	}
	
	if(GotEnCode)
	{
		return encodeType;
	}
	
	int strlength = strlen(pJudge);
	
	if(nLength != strlength)//unicode这边要判断是否是bigEncoding还是LittleEncoding
	{
		for(unsigned int i = 0; i < nLength; i++)
		{
			if((unsigned char) pJudge[i] == 0x00)
			{
				if(i%2 == 0)
				{
					encodeType = "UNICODEBIG";
					break;
				}
				else
				{
					encodeType = "UNICODELITTLE";
					break;
				}
			}
		}
	}
	else
	{
		char *pChangeLine = strchr(pJudge, '\n');
		char szChinese[MIN_CHINESE_COUNT + 1] = {0};
		char *pTemp = pJudge;
		int nCopyIndex = 0;
		while((unsigned char)(*pTemp) < 0x80 && *pTemp != '\0')
		{
			pTemp++;
		}
		
		while(pTemp && *pTemp != '\0' && nCopyIndex < MIN_CHINESE_COUNT)
		{
			if((unsigned char)(*pTemp) >= 0x80)
			{
				szChinese[nCopyIndex] = *pTemp;
				nCopyIndex++;
			}
			pTemp++;
		}
		
		if(isUTF8(szChinese))
		{
			encodeType = "UTF8";
			return encodeType;
		}
		else
		{
			if(pChangeLine && ((unsigned char)szChinese[0]) <= 0x80)
			{
				pTemp = pChangeLine;
				while((unsigned char)(*pTemp) <= 0x80 && *pTemp != '\0')
					pTemp++;
				if(strlen(pTemp) > MIN_CHINESE_COUNT)
				{
					memcpy(szChinese, pTemp, MIN_CHINESE_COUNT*sizeof(char));
					if(isUTF8(szChinese))
					{
						encodeType = "UTF8";
						return encodeType;
					}
				}
			}
		}
		encodeType = "GBK";
	}
	//if ( encodeType == "GBK" )
	if ( strcmp(encodeType, "GBK") == 0 )
	{
		if ( hasBig5Char(textData) )
			encodeType = "BIG5";
		else
			encodeType = "GBK";
	}
	return encodeType;
}

static int textEncodeChange( char *srcEncode, char *destEncode, char* fromStr, int* fromLen, char* toStr, int* toLen )
{
	
	char* fromCodec = srcEncode;
	char* toCodec = destEncode;
	
	int totalToLen = *toLen;
	iconv_t hIconv = iconv_open( toCodec, fromCodec );
	if ( hIconv == (iconv_t)-1 )
	{
		printf( "iconv open err\n" );
		return -1;
	}
	size_t res = iconv( hIconv, (char**)&fromStr, (size_t*)fromLen, &toStr, (size_t*)toLen );
	if ( res == -1 )
	{
		perror("iconv failed");
	}
	iconv_close( hIconv );
	return totalToLen - (*toLen);
}

void changeCodeToUTF8(char *source, int size, char *dest, int destSize)
{
	char *srcEncode = getTextEncodeType((unsigned char*)source, size);
	int fromLen = size;
	int toLen = destSize;
	int len = textEncodeChange(srcEncode, "UTF8", source, &fromLen, dest, &toLen);
	if (len < destSize)
		dest[len] = 0;
}