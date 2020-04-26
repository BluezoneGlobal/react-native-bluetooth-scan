//
//  ScannerManagerModule.m
//  CPMSMobileApp
//
//  Created by Dũng Đình on 4/10/20.
//


#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(ScannerManager, RCTViewManager)
RCT_EXTERN_METHOD(onStartViaManager)
RCT_EXTERN_METHOD(onStopViaManager)
RCT_EXTERN_METHOD(onSetUserId:(NSString *) userId)
RCT_EXTERN_METHOD(onSetTimeDelay:(NSInteger *) userId)
@end

@interface RCT_EXTERN_MODULE(TraceCovid, RCTEventEmitter)
RCT_EXTERN_METHOD(onGetUUId: (String)value)
@end
