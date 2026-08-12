export type ScreenType =
  | 'login'
  | 'register'
  | 'register_success'
  | 'connect'
  | 'pair_request'
  | 'connected_forever'
  | 'relationship_setup'
  | 'home'
  | 'update_status'
  | 'mood'
  | 'updates'
  | 'birthday'
  | 'profile'
  | 'edit_profile'
  | 'settings'
  | 'unpair_modal';

export interface UserProfile {
  username: string;
  phone: string;
  dob: string;
  avatarUrl: string;
  partnerNickname: string;
  partnerId: string;
  relationshipStartDate: string;
  currentMood: string;
  currentStatus: string;
  statusMessage: string;
}

export interface PartnerProfile {
  name: string;
  nickname: string;
  avatarUrl: string;
  partnerId: string;
  dob: string;
  online: boolean;
  currentMood: string;
  currentStatus: string;
  statusMessage: string;
}

export interface UpdateNotification {
  id: string;
  title: string;
  time: string;
  description: string;
  icon: string;
  colorType: 'primary' | 'secondary' | 'tertiary';
}

export interface WishlistItem {
  id: string;
  title: string;
  price?: string;
  link?: string;
}
