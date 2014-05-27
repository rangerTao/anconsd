//
//  MIUtility.m
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-28.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import "MIUtility.h"
#include <dlfcn.h>

#define INSTALLATION_FRAMEWORK_PATH @"PrivateFrameworks/MobileInstallation.framework/MobileInstallation"

@implementation MIUtility

static int *mi_utility_browse_callback(NSDictionary *dict, NSMutableArray *result) {
	NSArray *currentlist = [dict objectForKey:@"CurrentList"];
	if (currentlist != nil) {
		if ([result isKindOfClass:[NSArray class]]) {
            for (NSDictionary *appInfo in currentlist) 
            {
                [result addObject:appInfo];
            }
		}
	}
	return 0;
}

static void mi_utility_install_callback(NSDictionary *info, NSError **error)
{
    NSString *errMessage = [info objectForKey:@"Error"];    
    if (errMessage)
    {
        if (error)
        {
            *error = [NSError errorWithDomain:@"MobileInstallation" code:0 
                                     userInfo:[NSDictionary dictionaryWithObject:errMessage forKey:NSLocalizedDescriptionKey]];
        }
    }
    else
    {        
        int percent = [[info objectForKey:@"PercentComplete"] intValue];
        NSString *status = [info objectForKey:@"Status"];
        
        if ([status isEqualToString:@"Complete"])
        {
            NSLog(@"install complete\n");
        }
        else
        {
            NSLog(@"Percent Complete %2d%%, Status %s\n", percent, [status UTF8String]);            
        }
    }    
}

static void mi_utility_uninstall_callback(NSDictionary *info, NSError **error)
{
    NSString *errMessage = [info objectForKey:@"Error"];    
    if (errMessage)
    {
        if (error)
        {
            *error = [NSError errorWithDomain:@"MobileInstallation" code:0 
                                     userInfo:[NSDictionary dictionaryWithObject:errMessage forKey:NSLocalizedDescriptionKey]];
        }
    }
    else
    {        
        int percent = [[info objectForKey:@"PercentComplete"] intValue];
        NSString *status = [info objectForKey:@"Status"];
        
        if ([status isEqualToString:@"Complete"])
        {
            NSLog(@"install complete\n");
        }
        else
        {
            NSLog(@"Percent Complete %2d%%, Status %s\n", percent, [status UTF8String]);            
        }
    }    
}

+ (void *)openMobileInstallation
{
    NSString *frameworkPath = [[NSBundle bundleWithIdentifier:@"com.apple.Foundation"] bundlePath];
    frameworkPath = [frameworkPath stringByDeletingLastPathComponent];
    frameworkPath = [frameworkPath stringByReplacingOccurrencesOfString:[frameworkPath lastPathComponent] withString:INSTALLATION_FRAMEWORK_PATH];
    //open mobileinstallation framework
    void *mobileInstallationLib = dlopen([frameworkPath UTF8String],RTLD_LAZY);
    return mobileInstallationLib;
}

+ (void)closeMobileInstallation:(void *)mobileInstallationLib
{
    //close framework
    dlclose(mobileInstallationLib);	
}

+ (NSArray *)allInstalledAppInfo
{
    
    NSMutableArray *allAppsArray = [NSMutableArray array];
    
    void *mobileInstallationLib = [self openMobileInstallation];
    //load browse app process address
    int (*symMobileInstallBrowse)(CFDictionaryRef dict, void* pfn, void *usercon) = NULL;    
    symMobileInstallBrowse = dlsym(mobileInstallationLib, "MobileInstallationBrowse");
    
    //get all installed user apps info
	if (symMobileInstallBrowse != NULL) {
        //here [Any, System, User] choose one
		symMobileInstallBrowse((CFDictionaryRef)[NSDictionary dictionaryWithObject:@"User" forKey:@"ApplicationType"],
                               mi_utility_browse_callback, allAppsArray);
	}
    
    [self closeMobileInstallation:mobileInstallationLib];
    return allAppsArray;
}
+ (NSDictionary *)dicForAllIntalledAppInfo
{
    NSArray *arr = [self allInstalledAppInfo];
    NSMutableDictionary *dic = nil;
    if (arr != nil && [arr count] != 0) {
        dic = [NSMutableDictionary dictionaryWithCapacity:[arr count]];
    }
    for (NSDictionary *appDic in arr) {
        NSString *identifier = [appDic objectForKey:@"CFBundleIdentifier"];
        [dic setObject:appDic forKey:identifier];
    }
    return dic;
}
+ (BOOL)install:(NSString *)filePath error:(NSError **)error
{
    int res = -1;
    
    void *mobileInstallationLib = [self openMobileInstallation];
    
    //load install process address
    int (*symMobileInstallationInstall)(NSString *path, NSDictionary *dict, void *na, void *param) = NULL;
    symMobileInstallationInstall = dlsym(mobileInstallationLib, "MobileInstallationInstall");
    
    //install
    if (symMobileInstallationInstall != NULL)
    {
        NSMutableDictionary* dict = [NSMutableDictionary dictionaryWithObject:@"User" forKey:@"ApplicationType"];
        res = symMobileInstallationInstall(filePath, dict, mi_utility_install_callback, error);    
    }
    
    [self closeMobileInstallation:mobileInstallationLib];
    return (res < 0) ? NO : YES;
}

+ (BOOL)uninstall:(NSString *)identifier error:(NSError **)error
{
    int res = -1;
    
    void *mobileInstallationLib = [self openMobileInstallation];
    
    //load uninstall process address
    int (*symMobileInstallationUninstall)(NSString *identifier, NSDictionary *dict, void *callback, void *param) = NULL;
    symMobileInstallationUninstall = dlsym(mobileInstallationLib, "MobileInstallationUninstall");
    
    //uninstall
    if (symMobileInstallationUninstall != NULL)
    {
        res = symMobileInstallationUninstall(identifier, NULL, mi_utility_uninstall_callback, error);
    }

    return (res < 0) ? NO : YES;
}
@end
