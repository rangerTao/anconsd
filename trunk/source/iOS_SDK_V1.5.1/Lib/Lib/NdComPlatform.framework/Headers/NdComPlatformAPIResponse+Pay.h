//
//  NdComPlatformAPIResponse.h
//  NdComPlatform_SNS
//
//  Created by Sie Kensou on 10-10-8.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <UIKit/UIKit.h>


extern NSString * const kNdCPBuyResultNotification;				/**< 购买结果的通知,在购买结束时会发送该通知。 */
extern NSString * const kNdCPAsynPaySMSSentNotification;		/**< 异步购买选择短信充值，在短信发送成功时会发送该通知 */


/**
 @brief 91豆支付信息
 @note 购买价格保留2位小数
 */
@interface NdBuyInfo : NSObject
{
	NSString *cooOrderSerial;
	NSString *productId;
	NSString *productName;
	float	 productPrice;			
	float	 productOrignalPrice;	
	unsigned int productCount;			
	NSString *payDescription;			
}

@property (nonatomic, retain) NSString *cooOrderSerial;				/**< 合作商的订单号，必须保证唯一，双方对账的唯一标记（用GUID生成，32位）*/
@property (nonatomic, retain) NSString *productId;					/**< 商品Id */
@property (nonatomic, retain) NSString *productName;				/**< 商品名字 */
@property (nonatomic, assign) float productPrice;					/**< 商品价格，两位小数 */
@property (nonatomic, assign) float productOrignalPrice;			/**< 商品原始价格，保留两位小数 */
@property (nonatomic, assign) unsigned int productCount;			/**< 购买商品个数 */
@property (nonatomic, retain) NSString *payDescription;				/**< 购买描述，可选，没有为空 */

- (BOOL)isValidBuyInfo;						/**<  判断支付信息是否有效 */
- (BOOL)isCostGreaterThanThreshold;			/**<  返回（总价>100W || 单价> 100W || 数量 > 100W*100） */
- (BOOL)isCostGreaterThanValue:(float)fValue;/**<  返回（productPrice * productCount > fValue) */

@end

