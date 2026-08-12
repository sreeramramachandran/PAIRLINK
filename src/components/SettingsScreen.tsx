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
    <div className="pt-24 pb-32 px-6 max-w-2xl mx-auto flex flex-col gap-6 relative z-10">
      {/* Page Header */}
      <div className="flex flex-col gap-1">
        <h2 className="font-display font-bold text-2xl md:text-3xl text-[#dae2fd]">
          Settings
        </h2>
        <p className="font-body text-sm text-[#d6c1c5]/80">
          Manage your shared sanctuary preferences.
        </p>
      </div>

      {/* Card 1: Joint Profile */}
      <section className="glass-panel rounded-2xl p-6 flex flex-col gap-4 relative overflow-hidden bg-white/10 border border-white/20">
        <div className="absolute -top-10 -right-10 w-32 h-32 bg-[#ffcbd5]/20 rounded-full blur-[40px] pointer-events-none" />
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-[#ffcbd5] text-2xl">
            person
          </span>
          <h3 className="font-display font-medium text-lg text-[#dae2fd]">
            Joint Profile
          </h3>
        </div>

        <div className="flex items-center justify-between py-2 border-b border-white/10">
          <div className="flex items-center gap-4">
            <div className="flex -space-x-3">
              <img
                src={partner.avatarUrl}
                alt="Partner"
                className="w-12 h-12 rounded-full border-2 border-[#0b1326] object-cover z-10"
              />
              <img
                src={userAvatar}
                alt="User"
                className="w-12 h-12 rounded-full border-2 border-[#0b1326] object-cover z-0"
              />
            </div>
            <div className="flex flex-col">
              <span className="font-body text-base text-[#dae2fd] font-semibold">
                Alex & {partner.name}
              </span>
              <span className="font-body text-xs text-[#d6c1c5]">
                Connected since 2022
              </span>
            </div>
          </div>
        </div>

        <button
          onClick={() => onNavigate('profile')}
          className="self-start mt-1 bg-[#ffcbd5] text-[#521f2e] rounded-full px-6 py-2.5 font-body text-xs font-bold hover:bg-[#ffd9e0] active:scale-95 transition-all shadow-[0_0_15px_rgba(244,167,185,0.4)] cursor-pointer"
        >
          Manage Profiles
        </button>
      </section>

      {/* Card 2: Privacy & Security */}
      <section className="glass-panel rounded-2xl p-6 flex flex-col gap-4 bg-white/10 border border-white/20">
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-[#ffcbd5] text-2xl">
            shield
          </span>
          <h3 className="font-display font-medium text-lg text-[#dae2fd]">
            Privacy & Security
          </h3>
        </div>

        <div className="flex flex-col gap-2 mt-1">
          <button
            onClick={() => alert('PairLink end-to-end privacy policy active.')}
            className="flex items-center justify-between py-3 px-2 rounded-lg hover:bg-white/5 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-sm text-[#dae2fd] group-hover:text-[#ffcbd5] transition-colors">
              Privacy Policy
            </span>
            <span className="material-symbols-outlined text-[#d6c1c5] text-sm">
              chevron_right
            </span>
          </button>

          <div className="h-[1px] w-full bg-white/10" />

          <button
            onClick={() => alert('Password change request link sent to your registered contact!')}
            className="flex items-center justify-between py-3 px-2 rounded-lg hover:bg-white/5 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-sm text-[#dae2fd] group-hover:text-[#ffcbd5] transition-colors">
              Change Password
            </span>
            <span className="material-symbols-outlined text-[#d6c1c5] text-sm">
              chevron_right
            </span>
          </button>

          <div className="h-[1px] w-full bg-white/10" />

          <div className="flex items-center justify-between py-2 px-2">
            <div className="flex flex-col">
              <span className="font-body text-sm text-[#dae2fd]">
                Presence Vibration
              </span>
              <span className="font-body text-xs text-[#d6c1c5]">
                Haptic feedback when partner is active
              </span>
            </div>
            <button
              type="button"
              onClick={() => setPresenceHaptics(!presenceHaptics)}
              className={`relative w-12 h-6 rounded-full border border-white/20 transition-colors flex items-center px-1 cursor-pointer ${
                presenceHaptics ? 'bg-[#ffcbd5]/40 border-[#ffcbd5]' : 'bg-white/10'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full transition-transform ${
                  presenceHaptics
                    ? 'bg-[#ffcbd5] translate-x-6 shadow-[0_0_8px_rgba(244,167,185,0.8)]'
                    : 'bg-[#d6c1c5] translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>
      </section>

      {/* Card 3: Shared Reminders */}
      <section className="glass-panel rounded-2xl p-6 flex flex-col gap-4 bg-white/10 border border-white/20">
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-[#ffcbd5] text-2xl">
            notifications
          </span>
          <h3 className="font-display font-medium text-lg text-[#dae2fd]">
            Shared Reminders
          </h3>
        </div>

        <div className="flex flex-col gap-4 mt-1">
          <div className="flex items-center justify-between px-2">
            <div className="flex flex-col">
              <span className="font-body text-sm text-[#dae2fd]">
                Daily Check-ins
              </span>
              <span className="font-body text-xs text-[#d6c1c5]">
                Prompt for daily mood updates
              </span>
            </div>
            <button
              type="button"
              onClick={() => setDailyCheckins(!dailyCheckins)}
              className={`relative w-12 h-6 rounded-full border border-white/20 transition-colors flex items-center px-1 cursor-pointer ${
                dailyCheckins ? 'bg-[#ffcbd5]/40 border-[#ffcbd5]' : 'bg-white/10'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full transition-transform ${
                  dailyCheckins
                    ? 'bg-[#ffcbd5] translate-x-6 shadow-[0_0_8px_rgba(244,167,185,0.8)]'
                    : 'bg-[#d6c1c5] translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="flex items-center justify-between px-2">
            <div className="flex flex-col">
              <span className="font-body text-sm text-[#dae2fd]">
                Anniversaries & Dates
              </span>
              <span className="font-body text-xs text-[#d6c1c5]">
                Calendar event notifications
              </span>
            </div>
            <button
              type="button"
              onClick={() => setAnniversaryNotifs(!anniversaryNotifs)}
              className={`relative w-12 h-6 rounded-full border border-white/20 transition-colors flex items-center px-1 cursor-pointer ${
                anniversaryNotifs ? 'bg-[#ffcbd5]/40 border-[#ffcbd5]' : 'bg-white/10'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full transition-transform ${
                  anniversaryNotifs
                    ? 'bg-[#ffcbd5] translate-x-6 shadow-[0_0_8px_rgba(244,167,185,0.8)]'
                    : 'bg-[#d6c1c5] translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>
      </section>

      {/* Card 4: App Settings & Danger Zone */}
      <section className="glass-panel rounded-2xl p-6 flex flex-col gap-4 bg-white/10 border border-white/20">
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-[#ffcbd5] text-2xl">
            settings
          </span>
          <h3 className="font-display font-medium text-lg text-[#dae2fd]">
            App Settings
          </h3>
        </div>

        <div className="flex flex-col gap-2 mt-1">
          <button
            onClick={() => alert('Appearance theme is set to Dark Glass Sanctuary.')}
            className="flex items-center justify-between py-3 px-2 rounded-lg hover:bg-white/5 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-sm text-[#dae2fd] group-hover:text-[#ffcbd5] transition-colors">
              Appearance
            </span>
            <div className="flex items-center gap-2 text-[#d6c1c5]">
              <span className="font-body text-xs">Dark (Default)</span>
              <span className="material-symbols-outlined text-sm">
                chevron_right
              </span>
            </div>
          </button>

          <div className="h-[1px] w-full bg-white/10" />

          <button
            onClick={() => alert('Data synchronized with encrypted local sanctuary storage.')}
            className="flex items-center justify-between py-3 px-2 rounded-lg hover:bg-white/5 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-sm text-[#dae2fd] group-hover:text-[#ffcbd5] transition-colors">
              Data Sync & Backup
            </span>
            <span className="material-symbols-outlined text-[#d6c1c5] text-sm">
              chevron_right
            </span>
          </button>

          <div className="h-[1px] w-full bg-white/10" />

          <button
            onClick={() => onNavigate('unpair_modal')}
            className="flex items-center justify-between py-3 px-2 rounded-lg hover:bg-rose-500/10 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-sm text-rose-300 font-medium">
              Unlink Partner
            </span>
            <span className="material-symbols-outlined text-rose-300 text-sm">
              heart_broken
            </span>
          </button>

          <div className="h-[1px] w-full bg-white/10" />

          <button
            onClick={onLogout}
            className="flex items-center justify-between py-3 px-2 rounded-lg hover:bg-rose-500/10 transition-colors group cursor-pointer text-left"
          >
            <span className="font-body text-sm text-rose-300 font-medium">
              Logout
            </span>
            <span className="material-symbols-outlined text-rose-300 text-sm">
              logout
            </span>
          </button>
        </div>
      </section>
    </div>
  );
};
