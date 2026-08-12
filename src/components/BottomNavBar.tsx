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
  // Hide bottom nav on full onboarding screens or modals
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
  const isMood = currentScreen === 'mood' || currentScreen === 'update_status';
  const isUpdates = currentScreen === 'updates' || currentScreen === 'birthday';
  const isSettings = currentScreen === 'settings' || currentScreen === 'profile' || currentScreen === 'edit_profile';

  return (
    <nav className="fixed bottom-6 left-0 right-0 z-40 flex justify-center items-center px-6 pointer-events-none">
      <div className="pointer-events-auto w-full max-w-sm rounded-full backdrop-blur-3xl border border-white/20 shadow-[0_8px_32px_0_rgba(0,0,0,0.4)] bg-white/10 dark:bg-white/10 flex justify-around items-center p-2 glass-card">
        {/* Home Tab */}
        <button
          onClick={() => onNavigate('home')}
          className={`p-3 rounded-full transition-all duration-200 flex items-center justify-center ${
            isHome
              ? 'bg-[#ffcbd5]/20 text-[#ffcbd5] shadow-[0_0_16px_rgba(244,167,185,0.5)] scale-105'
              : 'text-[#d6c1c5] hover:text-[#ffcbd5] hover:bg-white/5'
          }`}
          title="Home Sanctuary"
        >
          <span
            className="material-symbols-outlined text-[24px]"
            data-weight={isHome ? 'fill' : 'none'}
            style={{ fontVariationSettings: isHome ? "'FILL' 1" : "'FILL' 0" }}
          >
            home
          </span>
        </button>

        {/* Mood Tab */}
        <button
          onClick={() => onNavigate('mood')}
          className={`p-3 rounded-full transition-all duration-200 flex items-center justify-center ${
            isMood
              ? 'bg-[#ffcbd5]/20 text-[#ffcbd5] shadow-[0_0_16px_rgba(244,167,185,0.5)] scale-105'
              : 'text-[#d6c1c5] hover:text-[#ffcbd5] hover:bg-white/5'
          }`}
          title="Mood & Status"
        >
          <span
            className="material-symbols-outlined text-[24px]"
            data-weight={isMood ? 'fill' : 'none'}
            style={{ fontVariationSettings: isMood ? "'FILL' 1" : "'FILL' 0" }}
          >
            mood
          </span>
        </button>

        {/* Updates / Timeline Tab */}
        <button
          onClick={() => onNavigate('updates')}
          className={`p-3 rounded-full transition-all duration-200 flex items-center justify-center ${
            isUpdates
              ? 'bg-[#ffcbd5]/20 text-[#ffcbd5] shadow-[0_0_16px_rgba(244,167,185,0.5)] scale-105'
              : 'text-[#d6c1c5] hover:text-[#ffcbd5] hover:bg-white/5'
          }`}
          title="Updates & Milestones"
        >
          <span
            className="material-symbols-outlined text-[24px]"
            data-weight={isUpdates ? 'fill' : 'none'}
            style={{ fontVariationSettings: isUpdates ? "'FILL' 1" : "'FILL' 0" }}
          >
            calendar_today
          </span>
        </button>

        {/* Settings / Profile Tab */}
        <button
          onClick={() => onNavigate('settings')}
          className={`p-3 rounded-full transition-all duration-200 flex items-center justify-center ${
            isSettings
              ? 'bg-[#ffcbd5]/20 text-[#ffcbd5] shadow-[0_0_16px_rgba(244,167,185,0.5)] scale-105'
              : 'text-[#d6c1c5] hover:text-[#ffcbd5] hover:bg-white/5'
          }`}
          title="Settings & Profile"
        >
          <span
            className="material-symbols-outlined text-[24px]"
            data-weight={isSettings ? 'fill' : 'none'}
            style={{ fontVariationSettings: isSettings ? "'FILL' 1" : "'FILL' 0" }}
          >
            settings
          </span>
        </button>
      </div>
    </nav>
  );
};
