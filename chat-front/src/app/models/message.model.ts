export interface Message {
  id : string;
  channelId: string;
  sender: string;
  message: string;
  serverTime: Date;
  editTime: Date;
  senderId : string;
}