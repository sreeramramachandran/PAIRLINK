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
        className="w-10 h-10 rounded-full bg-white/90 backdrop-blur-2xl border border-white text-[#E60039] shadow-md flex items-center justify-center transition-all active:scale-90 cursor-pointer hover:bg-rose-50"
        title="Screen Navigator / View All Screens"
      >
        <span className="material-symbols-outlined text-[20px]">
          dashboard
        </span>
      </button>

      {/* Slide-out Menu */}
      {isOpen && (
        <div className="absolute right-0 top-12 w-64 bg-white/95 backdrop-blur-2xl border border-white/90 rounded-3xl p-4 shadow-[0_16px_40px_rgba(230,0,57,0.15)] max-h-[80vh] overflow-y-auto z-50 text-left modal-animate">
          <div className="flex items-center justify-between pb-3 border-b border-rose-100 mb-3">
            <span className="font-display font-extrabold text-xs text-[#E60039] uppercase tracking-wider">
              PairLink Screens (16)
            </span>
            <button
              onClick={() => setIsOpen(false)}
              className="text-[#7A6E73] hover:text-[#E60039] text-sm font-bold"
            >
              ✕
            </button>
          </div>

          <div className="space-y-3">
            {['Auth & Onboarding', 'Main Sanctuary', 'Profile & Settings'].map((groupName) => (
              <div key={groupName} className="space-y-1">
                <p className="font-body text-[10px] text-[#7A6E73] uppercase tracking-widest font-bold px-2 pt-1">
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
                      className={`w-full text-left px-3 py-1.5 rounded-xl text-xs font-body transition-colors cursor-pointer flex items-center justify-between ${
                        isActive
                          ? 'bg-rose-50 text-[#E60039] font-bold border border-rose-200'
                          : 'text-[#2D2226] hover:bg-rose-50/50'
                      }`}
                    >
                      <span>{screen.label}</span>
                      {isActive && <span className="text-[10px] text-[#E60039]">●</span>}
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
