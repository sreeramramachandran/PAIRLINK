import React, { useState } from 'react';
import { ScreenType } from '../types';

interface QuickScreenSwitcherProps {
  currentScreen: ScreenType;
  onNavigate: (screen: ScreenType) => void;
}

const ALL_SCREENS: { id: ScreenType; label: string; group: string }[] = [
  // Onboarding & Pair Flow
  { id: 'login', label: '1. Login', group: 'Auth & Onboarding' },
  { id: 'register', label: '2. Register', group: 'Auth & Onboarding' },
  { id: 'register_success', label: '3. Registration Success', group: 'Auth & Onboarding' },
  { id: 'connect', label: '4. Connect Partner ID', group: 'Auth & Onboarding' },
  { id: 'pair_request', label: '5. Pair Request Modal', group: 'Auth & Onboarding' },
  { id: 'connected_forever', label: '6. Connected Forever', group: 'Auth & Onboarding' },
  { id: 'relationship_setup', label: '7. Personalize Journey', group: 'Auth & Onboarding' },

  // Main Features
  { id: 'home', label: '8. Home Sanctuary', group: 'Main Sanctuary' },
  { id: 'update_status', label: '9. Quick Update Status', group: 'Main Sanctuary' },
  { id: 'mood', label: '10. Mood Selector', group: 'Main Sanctuary' },
  { id: 'updates', label: '11. Updates / Timeline', group: 'Main Sanctuary' },
  { id: 'birthday', label: '12. Birthday Countdown', group: 'Main Sanctuary' },

  // Profile & Settings
  { id: 'profile', label: '13. Partner Profile', group: 'Profile & Settings' },
  { id: 'edit_profile', label: '14. Edit Profile', group: 'Profile & Settings' },
  { id: 'settings', label: '15. Settings', group: 'Profile & Settings' },
  { id: 'unpair_modal', label: '16. Unpair Modal', group: 'Profile & Settings' },
];

export const QuickScreenSwitcher: React.FC<QuickScreenSwitcherProps> = ({
  currentScreen,
  onNavigate,
}) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="fixed top-4 right-4 z-50">
      {/* Floating Toggle Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="glass-card text-[#ffcbd5] bg-[#0b1326]/80 hover:bg-[#ffcbd5]/20 border border-white/20 p-2.5 rounded-full shadow-[0_0_20px_rgba(244,167,185,0.4)] flex items-center justify-center transition-all active:scale-90 cursor-pointer"
        title="Screen Navigator / View All Screens"
      >
        <span className="material-symbols-outlined text-[20px]">
          dashboard
        </span>
      </button>

      {/* Slide-out Menu */}
      {isOpen && (
        <div className="absolute right-0 top-12 w-64 glass-card bg-[#0b1326]/90 backdrop-blur-3xl border border-white/20 rounded-2xl p-4 shadow-[0_16px_48px_rgba(0,0,0,0.6)] max-h-[80vh] overflow-y-auto z-50 text-left animate-enter">
          <div className="flex items-center justify-between pb-3 border-b border-white/10 mb-3">
            <span className="font-display font-bold text-xs text-[#ffcbd5] uppercase tracking-wider">
              PairLink Screens (16)
            </span>
            <button
              onClick={() => setIsOpen(false)}
              className="text-[#d6c1c5] hover:text-[#ffcbd5] text-sm"
            >
              ✕
            </button>
          </div>

          <div className="space-y-3">
            {['Auth & Onboarding', 'Main Sanctuary', 'Profile & Settings'].map((groupName) => (
              <div key={groupName} className="space-y-1">
                <p className="font-body text-[10px] text-[#d6c1c5]/60 uppercase tracking-widest font-semibold px-2 pt-1">
                  {groupName}
                </p>
                {ALL_SCREENS.filter((s) => s.group === groupName).map((screen) => {
                  const isActive = currentScreen === screen.id;
                  return (
                    <button
                      key={screen.id}
                      onClick={() => {
                        onNavigate(screen.id);
                        setIsOpen(false);
                      }}
                      className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-body transition-colors cursor-pointer flex items-center justify-between ${
                        isActive
                          ? 'bg-[#ffcbd5]/25 text-[#ffcbd5] font-semibold border border-[#ffcbd5]/40'
                          : 'text-[#dae2fd] hover:bg-white/10'
                      }`}
                    >
                      <span>{screen.label}</span>
                      {isActive && <span className="text-[10px]">●</span>}
                    </button>
                  );
                })}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
