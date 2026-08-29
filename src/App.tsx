import React, { useState } from 'react';
import { ShaderBackground } from './components/ShaderBackground';
import { TopAppBar } from './components/TopAppBar';
import { BottomNavBar } from './components/BottomNavBar';
import { QuickScreenSwitcher } from './components/QuickScreenSwitcher';

// Screens
import { LoginScreen } from './components/LoginScreen';
import { RegisterScreen } from './components/RegisterScreen';
import { RegisterSuccessScreen } from './components/RegisterSuccessScreen';
import { ConnectScreen } from './components/ConnectScreen';
import { PairRequestModal } from './components/PairRequestModal';
import { ConnectedForeverScreen } from './components/ConnectedForeverScreen';
import { RelationshipSetupScreen } from './components/RelationshipSetupScreen';
import { HomeScreen } from './components/HomeScreen';
import { UpdateStatusScreen } from './components/UpdateStatusScreen';
import { MoodScreen } from './components/MoodScreen';
import { UpdatesScreen } from './components/UpdatesScreen';
import { BirthdayScreen } from './components/BirthdayScreen';
import { ProfileScreen } from './components/ProfileScreen';
import { EditProfileScreen } from './components/EditProfileScreen';
import { SettingsScreen } from './components/SettingsScreen';
import { UnpairModal } from './components/UnpairModal';

import { ScreenType, UserProfile, PartnerProfile, UpdateNotification, WishlistItem } from './types';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<ScreenType>('home');

  // User State
  const [userProfile, setUserProfile] = useState<UserProfile>({
    username: 'Alex',
    phone: '9876543210',
    dob: '1995-08-15',
    avatarUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuAPHWdbs4YQUNnS04U_nwz7Bpf9x-1oj9ppECEWgv5PcMqIBnhkrbrahsSjUdqmJROOHDxuJe_w0h7BAbrSTaQ7FFjaE99sIMIZC-Fpirmdazi7piFRP-vc36V6bYLDQhRnUbHwhFWLzo_AKL8yNrGcJYSvx393_gAMXdbOIazSg7PiA1zFfZRROi3emEm5lpCWQwXYRRcjIP2XNDyjBBTfe-l4Am6Vow_uR6aArpDVJxogZvEPMp3wiQ',
    partnerNickname: 'My Love',
    partnerId: 'HEART-482761',
    relationshipStartDate: '2022-06-14',
    currentMood: 'Loving',
    currentStatus: 'At Work',
    statusMessage: 'Thinking about our next date night.',
  });

  // Partner State
  const [partnerProfile, setPartnerProfile] = useState<PartnerProfile>({
    name: 'Sreeram',
    nickname: 'My Moon & Stars',
    avatarUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuA2IDokHktIyO6b8PsuaYQ7IlH4vC_AyEZNwoprESOar4LCzo3679uaEE79Hvt7jVgbHdkctYrQ4NRwp9Lp4uVpkdCKadU3zRZnWNzQ9g5KVhrmWC6h6KVpspPWYPVINc8KOFsevhOhNCyV-oFkSvHoRoXQXZTSN8WdKCdqHoo5ZqjFvCb6sDAt76nTTQVWE8De_kdAExovAh19ILylEGe7L8ZAs21U33y0vP_BwEBItgmoCK2pdM0VPg',
    partnerId: '#PL-8492',
    dob: '1997-10-24',
    online: true,
    currentMood: 'Feeling Serene',
    currentStatus: 'At Work',
    statusMessage:
      'Taking a quiet evening to read and recharge. Can\'t wait to see you tomorrow! ❤️',
  });

  // Interactive state
  const [heartPulseCount, setHeartPulseCount] = useState<number>(14);

  const [notifications, setNotifications] = useState<UpdateNotification[]>([
    {
      id: '1',
      title: 'Alex is thinking about you',
      time: 'Just now',
      description: 'They sent a little burst of affection.',
      icon: 'volunteer_activism',
      colorType: 'primary',
    },
    {
      id: '2',
      title: "Mood updated to 'Cozy'",
      time: '2 hrs ago',
      description: 'Alex is feeling relaxed and cozy at home.',
      icon: 'mood',
      colorType: 'secondary',
    },
    {
      id: '3',
      title: "Alex's Birthday Tomorrow!",
      time: 'Yesterday',
      description: "Don't forget to prepare something special.",
      icon: 'cake',
      colorType: 'tertiary',
    },
  ]);

  const [wishlist, setWishlist] = useState<WishlistItem[]>([
    { id: '1', title: 'Vintage Vinyl Record Player' },
    { id: '2', title: 'Aromatherapy Diffuser & Lavender Oil' },
    { id: '3', title: 'Custom Star Map Frame' },
  ]);

  // Actions
  const handleSendHeart = () => {
    setHeartPulseCount((prev) => prev + 1);
    // Add notification
    const newNotif: UpdateNotification = {
      id: Date.now().toString(),
      title: `${userProfile.username} sent a heartbeat!`,
      time: 'Just now',
      description: 'A warm burst of affection was sent to your sanctuary.',
      icon: 'favorite',
      colorType: 'primary',
    };
    setNotifications((prev) => [newNotif, ...prev]);
  };

  const handleUpdateMood = (newMood: string) => {
    setPartnerProfile((prev) => ({ ...prev, currentMood: newMood }));
    setNotifications((prev) => [
      {
        id: Date.now().toString(),
        title: `Mood updated to '${newMood}'`,
        time: 'Just now',
        description: `${partnerProfile.name} updated their mood in sanctuary.`,
        icon: 'mood',
        colorType: 'secondary',
      },
      ...prev,
    ]);
  };

  const handleUpdateStatus = (newStatus: string) => {
    setPartnerProfile((prev) => ({ ...prev, currentStatus: newStatus }));
  };

  const handleSaveProfile = (data: {
    name: string;
    nickname: string;
    dob: string;
    mood: string;
    statusMessage: string;
    avatarUrl?: string;
  }) => {
    setPartnerProfile((prev) => ({
      ...prev,
      name: data.name,
      nickname: data.nickname,
      dob: data.dob,
      currentMood: data.mood,
      statusMessage: data.statusMessage,
      avatarUrl: data.avatarUrl || prev.avatarUrl,
    }));
  };

  const handleAddWishlistItem = (title: string) => {
    setWishlist((prev) => [...prev, { id: Date.now().toString(), title }]);
  };

  // Screen header conditions
  const showTopHeader = [
    'home',
    'update_status',
    'mood',
    'updates',
    'birthday',
    'profile',
    'edit_profile',
    'settings',
  ].includes(currentScreen);

  const isEditProfile = currentScreen === 'edit_profile';

  return (
    <div className="relative min-h-screen bg-[#FAF5F5] text-[#2D2226] font-body selection:bg-[#E60039]/20 selection:text-[#E60039] overflow-x-hidden">
      {/* Interactive WebGL Shader Background */}
      <ShaderBackground />

      {/* Quick Screen Switcher Drawer for demo and testing */}
      <QuickScreenSwitcher
        currentScreen={currentScreen}
        onNavigate={setCurrentScreen}
      />

      {/* Top App Bar Header */}
      {showTopHeader && (
        <TopAppBar
          currentScreen={currentScreen}
          onNavigate={setCurrentScreen}
          partnerAvatar={partnerProfile.avatarUrl}
          partnerName={partnerProfile.name}
          isPartnerOnline={partnerProfile.online}
          onSendHeart={handleSendHeart}
          showBack={isEditProfile}
          onBack={() => setCurrentScreen('profile')}
          titleOverride={isEditProfile ? 'Edit Profile' : undefined}
        />
      )}

      {/* Screen Views */}
      <div className="relative z-10">
        {currentScreen === 'login' && (
          <LoginScreen
            onNavigate={setCurrentScreen}
            onLoginSuccess={(phone) => {
              setUserProfile((prev) => ({ ...prev, phone }));
            }}
          />
        )}

        {currentScreen === 'register' && (
          <RegisterScreen
            onNavigate={setCurrentScreen}
            onRegisterSuccess={(username, phone, dob) => {
              setUserProfile((prev) => ({ ...prev, username, phone, dob }));
            }}
          />
        )}

        {currentScreen === 'register_success' && (
          <RegisterSuccessScreen
            username={userProfile.username}
            partnerId={userProfile.partnerId}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'connect' && (
          <ConnectScreen
            onNavigate={setCurrentScreen}
            onConnectPartner={(targetId) => {
              setPartnerProfile((prev) => ({ ...prev, partnerId: targetId }));
            }}
          />
        )}

        {currentScreen === 'pair_request' && (
          <PairRequestModal
            partner={partnerProfile}
            onAccept={() => {
              setPartnerProfile((prev) => ({ ...prev, online: true }));
            }}
            onReject={() => {
              setCurrentScreen('connect');
            }}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'connected_forever' && (
          <ConnectedForeverScreen
            userAvatar={userProfile.avatarUrl}
            partnerAvatar={partnerProfile.avatarUrl}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'relationship_setup' && (
          <RelationshipSetupScreen
            onNavigate={setCurrentScreen}
            onSaveStartDate={(startDate) => {
              setUserProfile((prev) => ({
                ...prev,
                relationshipStartDate: startDate,
              }));
            }}
          />
        )}

        {currentScreen === 'home' && (
          <HomeScreen
            partner={partnerProfile}
            relationshipStartDate={userProfile.relationshipStartDate}
            onNavigate={setCurrentScreen}
            onSendHeartPulse={handleSendHeart}
            heartPulseCount={heartPulseCount}
          />
        )}

        {currentScreen === 'update_status' && (
          <UpdateStatusScreen
            currentStatus={partnerProfile.currentStatus}
            onUpdateStatus={handleUpdateStatus}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'mood' && (
          <MoodScreen
            currentMood={partnerProfile.currentMood}
            onSaveMood={handleUpdateMood}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'updates' && (
          <UpdatesScreen
            notifications={notifications}
            partnerName={partnerProfile.name}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'birthday' && (
          <BirthdayScreen
            partner={partnerProfile}
            wishlist={wishlist}
            onAddWishlistItem={handleAddWishlistItem}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'profile' && (
          <ProfileScreen
            partner={partnerProfile}
            userNickname={userProfile.partnerNickname}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'edit_profile' && (
          <EditProfileScreen
            partner={partnerProfile}
            userNickname={userProfile.partnerNickname}
            onSaveProfile={handleSaveProfile}
            onNavigate={setCurrentScreen}
          />
        )}

        {currentScreen === 'settings' && (
          <SettingsScreen
            partner={partnerProfile}
            userAvatar={userProfile.avatarUrl}
            onNavigate={setCurrentScreen}
            onLogout={() => {
              setCurrentScreen('login');
            }}
          />
        )}

        {currentScreen === 'unpair_modal' && (
          <UnpairModal
            onUnpairConfirm={() => {
              setNotifications((prev) => [
                {
                  id: Date.now().toString(),
                  title: 'Unpaired Partner',
                  time: 'Just now',
                  description: 'You have disconnected from your partner.',
                  icon: 'heart_broken',
                  colorType: 'tertiary',
                },
                ...prev,
              ]);
            }}
            onCancel={() => {
              setCurrentScreen('settings');
            }}
            onNavigate={setCurrentScreen}
          />
        )}
      </div>

      {/* Floating Capsule Bottom Navigation Bar */}
      <BottomNavBar
        currentScreen={currentScreen}
        onNavigate={setCurrentScreen}
      />
    </div>
  );
}
