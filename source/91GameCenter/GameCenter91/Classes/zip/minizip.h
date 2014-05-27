/**
 * @skip  $ld:$
 * @file  minizip.h
 * @brief zip definition
 * @date  2010/05/25 ND)Chenqsh Create
 *	
 *	All Rights Reserved, Copyright (C) ND LIMITED 2010
 */


/********************************************************************************************************************/
/*!
 *  @brief  zip files
 *  @note   This function treats following :\n
 *              zip specified files into filename.zip
 *  @param  argc		[in] zip parameter's numbers.
 *  @param  argv		[in] zip parameter.First parameter should be reserved.
 *						Usage : minizip [-o] [-a] [-0 to -9] [-p password] [-j] file.zip [files_to_add]
 *						-o  Overwrite existing file.zip
 *						-a  Append to existing file.zip
 *						-0  Store only
 *						-1  Compress faster
 *						-9  Compress better
 *						-j  exclude path. store only the file name.
 *  @param  codeConv	[in] if it's need to code convert.
 *						 0:needn't to convert 
 *						 1:UTF8->GB2312
 *  @return Result code is returned.
 *  @retval 0	:success
 *  @retval !0	:error
 *  @date  2010/05/25 ND)Chenqsh Create
 *
 */
/********************************************************************************************************************/

#define unix
#if defined __cplusplus
extern "C"
{
#endif
	
	int zipMain(int argc, char *argv[], char codeConv);
	
#if defined __cplusplus
}
#endif