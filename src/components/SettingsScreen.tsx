import React, { useState } from 'react';
import { ScreenType, PartnerProfile } from '../types';

interface SettingsScreenProps {
  partner: PartnerProfile;
  userAvatar: string;
  onNavigate: (screen: ScreenType) => void;
  onLogout: () => void;
}

export const SettingsScreen: React.FC<SettingsScreenProps> = ({
  partner,
  userAvatar,
  onNavigate,
  onLogout,
}) => {
  const [dailyCheckins, setDailyCheckins] = useState(true);
  const [anniversaryNotifs, setAnniversaryNotifs] = useState(true);
  const [presenceHaptics, setPresenceHaptics] = useState(false);

  return (
    <div className="pt-20 pb-32 px-4 sm:px-6 max-w-lg mx-auto flex flex-col gap-5 relative z-10">
      {/* Page Header */}
      <div className="flex flex-col gap-1 text-center sm:text-left pt-2">
        <h2 className="font-display font-extrabold text-2xl text-[#2D2226]">
          Settings & Preferences
        </h2>
        <p className="font-body text-xs text-[#7A6E73] font-medium">
          Manage your shared sanctuary preferences.
        </p>
      </div>

      {/* Card 1: Joint Profile */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-6 flex flex-col gap-4 border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <div className="flex items-center gap-2.5">
          <span className="material-symbols-outlined text-[#E60039] text-xl">
            person
          </span>
          <h3 className="font-display font-bold text-base text-[#2D2226]">
            Joint Profile
          </h3>
        </div>

        <div className="flex items-center justify-between py-2 border-b border-rose-100">
          <div className="flex items-center gap-3.5">
            <div className="flex -space-x-2">
              <img
                src={partner.avatarUrl}
                alt="Partner"
                className="w-11 h-11 rounded-full border-2 border-white object-cover shadow-sm z-10"
              />
              <img
                src={userAvatar}
                alt="User"
                className="w-11 h-11 rounded-full border-2 border-white object-cover shadow-sm z-0"
              />
            </div>
            <div className="flex flex-col">
              <span className="font-display text-sm text-[#2D2226] font-bold">
                Alex & {partner.name}
              </span>
              <span className="font-body text-[11px] text-[#7A6E73] font-medium">
                Connected since 2022
              </span>
            </div>
          </div>
        </div>

        <button
          onClick={() => onNavigate('profile')}
          className="glow-button self-start px-5 py-2 rounded-full font-display font-bold text-xs cursor-pointer active:scale-95 transition-all"
        >
          Manage Profiles
        </button>
      </section>

      {/* Card 2: Privacy & Security */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-6 flex flex-col gap-4 border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <div className="flex items-center gap-2.5">
          <span className="material-symbols-outlined text-[#E60039] text-xl">
            shield
          </span>
          <h3 className="font-display font-bold text-base text-[#2D2226]">
            Privacy & Security
          </h3>
        </div>

        <div className="flex flex-col gap-1">
          <button
            onClick={() => alert('PairLink end-to-end privacy policy active.')}
            className="flex items-center justify-between py-2.5 px-2 rounded-2xl hover:bg-rose-50/50 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-xs text-[#2D2226] font-bold group-hover:text-[#E60039]">
              Privacy Policy
            </span>
            <span className="material-symbols-outlined text-[#7A6E73] text-sm">
              chevron_right
            </span>
          </button>

          <div className="h-[1px] w-full bg-rose-100" />

          <button
            onClick={() => alert('Password change request link sent to your registered contact!')}
            className="flex items-center justify-between py-2.5 px-2 rounded-2xl hover:bg-rose-50/50 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-xs text-[#2D2226] font-bold group-hover:text-[#E60039]">
              Change Password
            </span>
            <span className="material-symbols-outlined text-[#7A6E73] text-sm">
              chevron_right
            </span>
          </button>

          <div className="h-[1px] w-full bg-rose-100" />

          <div className="flex items-center justify-between py-2.5 px-2">
            <div className="flex flex-col">
              <span className="font-body text-xs text-[#2D2226] font-bold">
                Presence Vibration
              </span>
              <span className="font-body text-[11px] text-[#7A6E73] font-medium">
                Haptic feedback when partner is active
              </span>
            </div>
            <button
              type="button"
              onClick={() => setPresenceHaptics(!presenceHaptics)}
              className={`relative w-11 h-6 rounded-full transition-colors flex items-center px-0.5 cursor-pointer ${
                presenceHaptics ? 'bg-[#E60039]' : 'bg-rose-200'
              }`}
            >
              <div
                className={`w-5 h-5 rounded-full bg-white transition-transform shadow-sm ${
                  presenceHaptics ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>
      </section>

      {/* Card 3: Shared Reminders */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-6 flex flex-col gap-4 border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <div className="flex items-center gap-2.5">
          <span className="material-symbols-outlined text-[#E60039] text-xl">
            notifications
          </span>
          <h3 className="font-display font-bold text-base text-[#2D2226]">
            Shared Reminders
          </h3>
        </div>

        <div className="flex flex-col gap-3">
          <div className="flex items-center justify-between px-2">
            <div className="flex flex-col">
              <span className="font-body text-xs text-[#2D2226] font-bold">
                Daily Check-ins
              </span>
              <span className="font-body text-[11px] text-[#7A6E73] font-medium">
                Prompt for daily mood updates
              </span>
            </div>
            <button
              type="button"
              onClick={() => setDailyCheckins(!dailyCheckins)}
              className={`relative w-11 h-6 rounded-full transition-colors flex items-center px-0.5 cursor-pointer ${
                dailyCheckins ? 'bg-[#E60039]' : 'bg-rose-200'
              }`}
            >
              <div
                className={`w-5 h-5 rounded-full bg-white transition-transform shadow-sm ${
                  dailyCheckins ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="flex items-center justify-between px-2">
            <div className="flex flex-col">
              <span className="font-body text-xs text-[#2D2226] font-bold">
                Anniversaries & Dates
              </span>
              <span className="font-body text-[11px] text-[#7A6E73] font-medium">
                Calendar event notifications
              </span>
            </div>
            <button
              type="button"
              onClick={() => setAnniversaryNotifs(!anniversaryNotifs)}
              className={`relative w-11 h-6 rounded-full transition-colors flex items-center px-0.5 cursor-pointer ${
                anniversaryNotifs ? 'bg-[#E60039]' : 'bg-rose-200'
              }`}
            >
              <div
                className={`w-5 h-5 rounded-full bg-white transition-transform shadow-sm ${
                  anniversaryNotifs ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>
      </section>

      {/* Card 4: App Actions */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-6 flex flex-col gap-3 border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <div className="flex items-center gap-2.5">
          <span className="material-symbols-outlined text-[#E60039] text-xl">
            settings
          </span>
          <h3 className="font-display font-bold text-base text-[#2D2226]">
            App Actions
          </h3>
        </div>

        <div className="flex flex-col gap-1">
          <button
            onClick={() => onNavigate('unpair_modal')}
            className="flex items-center justify-between py-2.5 px-3 rounded-2xl bg-rose-50/60 hover:bg-rose-100/80 transition-colors text-left cursor-pointer border border-rose-200/60"
          >
            <span className="font-body text-xs text-[#E60039] font-extrabold">
              Unlink Partner
            </span>
            <span className="material-symbols-outlined text-[#E60039] text-sm">
              heart_broken
            </span>
          </button>

          <button
            onClick={onLogout}
            className="flex items-center justify-between py-2.5 px-3 rounded-2xl bg-rose-50/60 hover:bg-rose-100/80 transition-colors text-left cursor-pointer border border-rose-200/60 mt-1"
          >
            <span className="font-body text-xs text-[#E60039] font-extrabold">
              Logout
            </span>
            <span className="material-symbols-outlined text-[#E60039] text-sm">
              logout
            </span>
          </button>
        </div>
      </section>
    </div>
  );
};
