/**
 * @skip  $ld:$
 * @file  miniunz.h
 * @brief unzip definition
 * @date  2010/05/25 ND)Chenqsh Create
 *	
 *	All Rights Reserved, Copyright (C) ND LIMITED 2010
 */


/********************************************************************************************************************/
/*!
 *  @brief  unzip zipFile
 *  @note   This function treats following :\n
 *              unzip specified zipFile to specified path
 *  @param  zipFileNameWithPath		[in] zipFile name include path.
 *  @param  extractPath				[in] path to extract files
 *  @param  passwordStr				[in] zipFile's password,if doesn't have password,then set NULL
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
	int unZipMain(char *zipFileNameWithPath,char *extractPath,char *passwordStr);

#if defined __cplusplus
}
#endif