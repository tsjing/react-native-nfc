import { NativeModules, DeviceEventEmitter } from "react-native";

const moduleName = "ReactNativeNFC";

export const StatusTypes = {
  ON: "ON",
  OFF: "OFF",
  NA: "NA"
};

export const EventTypes = {
  discovered: moduleName + "/DISCOVERED",
  on: moduleName + "/ON",
  off: moduleName + "/OFF"
};

export const getStatus = NativeModules[moduleName].getStatus;
export const addEventListener = DeviceEventEmitter.addListener;
