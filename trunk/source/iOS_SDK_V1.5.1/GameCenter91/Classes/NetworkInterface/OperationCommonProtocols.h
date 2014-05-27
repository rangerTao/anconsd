//
//  OperationCommonProtocols.h
//  GameCenter91
//
//  Created by  hiyo on 13-10-16.
//
//

@protocol OperationCommonProtocol <NSObject>

@required

@property (nonatomic, assign) id operationDelegate;
@property (nonatomic, retain) NSNumber *referenceNumber;

- (int)operation;
- (void)cancelOperation;
- (void)callProtocolMethodOnObject:(id)object;

@end
