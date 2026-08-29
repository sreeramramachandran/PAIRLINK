import React from 'react';
import { ScreenType } from '../types';

interface BottomNavBarProps {
  currentScreen: ScreenType;
  onNavigate: (screen: ScreenType) => void;
}

export const BottomNavBar: React.FC<BottomNavBarProps> = ({
  currentScreen,
  onNavigate,
}) => {
  // Hide bottom nav on onboarding screens or full screen modals
  const hideNavScreens: ScreenType[] = [
    'login',
    'register',
    'register_success',
    'connect',
    'pair_request',
    'connected_forever',
    'relationship_setup',
    'unpair_modal',
  ];

  if (hideNavScreens.includes(currentScreen)) {
    return null;
  }

  const isHome = currentScreen === 'home';
  const isChat = currentScreen === 'updates';
  const isMemories = currentScreen === 'birthday';
  const isProfile = currentScreen === 'profile' || currentScreen === 'edit_profile' || currentScreen === 'settings';

  return (
    <nav className="fixed bottom-4 left-0 right-0 z-40 flex justify-center items-center px-4 pointer-events-none">
      <div className="pointer-events-auto w-full max-w-md bg-white/95 backdrop-blur-2xl border border-white/90 shadow-[0_12px_36px_rgba(230,0,57,0.12),0_4px_16px_rgba(0,0,0,0.06)] rounded-full px-5 py-2 flex justify-between items-center">
        {/* 1. Home Tab */}
        <button
          onClick={() => onNavigate('home')}
          className={`flex flex-col items-center justify-center transition-all duration-200 group ${
            isHome ? 'text-[#E60039]' : 'text-[#7A6E73] hover:text-[#E60039]'
          }`}
          title="Home Sanctuary"
        >
          <span
            className="material-symbols-outlined text-[24px] transition-transform group-hover:scale-110"
            style={{ fontVariationSettings: isHome ? "'FILL' 1" : "'FILL' 0" }}
          >
            home
          </span>
          <span className="text-[10px] font-bold tracking-tight mt-0.5">Home</span>
          {isHome && <span className="w-1 h-1 rounded-full bg-[#E60039] mt-0.5" />}
        </button>

        {/* 2. Chat Tab */}
        <button
          onClick={() => onNavigate('updates')}
          className={`flex flex-col items-center justify-center transition-all duration-200 group ${
            isChat ? 'text-[#E60039]' : 'text-[#7A6E73] hover:text-[#E60039]'
          }`}
          title="Chat & Updates"
        >
          <span
            className="material-symbols-outlined text-[24px] transition-transform group-hover:scale-110"
            style={{ fontVariationSettings: isChat ? "'FILL' 1" : "'FILL' 0" }}
          >
            chat_bubble
          </span>
          <span className="text-[10px] font-bold tracking-tight mt-0.5">Chat</span>
          {isChat && <span className="w-1 h-1 rounded-full bg-[#E60039] mt-0.5" />}
        </button>

        {/* 3. Center Raised Floating Heart Button */}
        <button
          onClick={() => onNavigate('mood')}
          className="relative -mt-7 flex flex-col items-center group active:scale-95 transition-transform"
          title="Express Love & Mood"
        >
          <div className="w-13 h-13 rounded-full bg-gradient-to-tr from-[#E60039] to-[#FF3366] text-white flex items-center justify-center shadow-lg shadow-red-500/35 border-[3.5px] border-[#FAF5F5] group-hover:scale-105 transition-transform">
            <span
              className="material-symbols-outlined text-[26px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
          </div>
        </button>

        {/* 4. Memories Tab */}
        <button
          onClick={() => onNavigate('birthday')}
          className={`flex flex-col items-center justify-center transition-all duration-200 group ${
            isMemories ? 'text-[#E60039]' : 'text-[#7A6E73] hover:text-[#E60039]'
          }`}
          title="Memories & Wishlist"
        >
          <span
            className="material-symbols-outlined text-[24px] transition-transform group-hover:scale-110"
            style={{ fontVariationSettings: isMemories ? "'FILL' 1" : "'FILL' 0" }}
          >
            photo_library
          </span>
          <span className="text-[10px] font-bold tracking-tight mt-0.5">Memories</span>
          {isMemories && <span className="w-1 h-1 rounded-full bg-[#E60039] mt-0.5" />}
        </button>

        {/* 5. Profile Tab */}
        <button
          onClick={() => onNavigate('profile')}
          className={`flex flex-col items-center justify-center transition-all duration-200 group ${
            isProfile ? 'text-[#E60039]' : 'text-[#7A6E73] hover:text-[#E60039]'
          }`}
          title="Profile & Settings"
        >
          <span
            className="material-symbols-outlined text-[24px] transition-transform group-hover:scale-110"
            style={{ fontVariationSettings: isProfile ? "'FILL' 1" : "'FILL' 0" }}
          >
            person
          </span>
          <span className="text-[10px] font-bold tracking-tight mt-0.5">Profile</span>
          {isProfile && <span className="w-1 h-1 rounded-full bg-[#E60039] mt-0.5" />}
        </button>
      </div>
    </nav>
  );
};
