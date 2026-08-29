import React from 'react';
import { ScreenType, PartnerProfile } from '../types';

interface ProfileScreenProps {
  partner: PartnerProfile;
  userNickname: string;
  onNavigate: (screen: ScreenType) => void;
}

export const ProfileScreen: React.FC<ProfileScreenProps> = ({
  partner,
  userNickname,
  onNavigate,
}) => {
  return (
    <div className="pt-20 pb-32 px-4 sm:px-6 max-w-lg mx-auto flex flex-col gap-5 relative z-10">
      {/* Hero Profile Card */}
      <section className="bg-white/90 backdrop-blur-2xl border border-white/90 rounded-3xl p-7 flex flex-col items-center text-center relative overflow-hidden shadow-[0_12px_36px_-6px_rgba(230,0,57,0.08)]">
        <div className="relative mb-4">
          <div className="w-28 h-28 rounded-full overflow-hidden border-4 border-white shadow-xl p-0.5 bg-white">
            <img
              src={partner.avatarUrl}
              alt={partner.name}
              className="w-full h-full rounded-full object-cover"
            />
          </div>
        </div>

        <h1 className="font-display font-extrabold text-2xl text-[#2D2226] mb-0.5">
          {partner.name}
        </h1>
        <p className="font-body text-sm text-[#E60039] italic font-semibold mb-5">
          "{userNickname || partner.nickname || 'My Moon & Stars'}"
        </p>

        <button
          onClick={() => onNavigate('edit_profile')}
          className="glow-button px-6 py-2.5 rounded-full font-display font-bold text-xs flex items-center gap-2 tracking-wide cursor-pointer active:scale-95 transition-all"
        >
          <span className="material-symbols-outlined text-[18px]">edit</span>
          <span>Edit Profile</span>
        </button>
      </section>

      {/* Bento Grid Details */}
      <section className="grid grid-cols-2 gap-4">
        {/* Partner ID */}
        <div className="bg-white/90 backdrop-blur-2xl border border-white/90 rounded-3xl p-5 flex flex-col justify-center items-start gap-1.5 shadow-[0_8px_24px_rgba(230,0,57,0.05)]">
          <span className="material-symbols-outlined text-[#E60039] text-[20px]">
            badge
          </span>
          <div>
            <p className="font-body text-[10px] text-[#7A6E73] uppercase tracking-wider font-bold">
              Partner ID
            </p>
            <p className="font-display text-base font-extrabold text-[#2D2226] mt-0.5">
              {partner.partnerId}
            </p>
          </div>
        </div>

        {/* Birthday */}
        <div className="bg-white/90 backdrop-blur-2xl border border-white/90 rounded-3xl p-5 flex flex-col justify-center items-start gap-1.5 shadow-[0_8px_24px_rgba(230,0,57,0.05)]">
          <span className="material-symbols-outlined text-[#E60039] text-[20px]">
            cake
          </span>
          <div>
            <p className="font-body text-[10px] text-[#7A6E73] uppercase tracking-wider font-bold">
              Birthday
            </p>
            <p className="font-display text-base font-extrabold text-[#2D2226] mt-0.5">
              Oct 14
            </p>
          </div>
        </div>
      </section>

      {/* Status & Mood Card */}
      <section className="bg-white/90 backdrop-blur-2xl border border-white/90 rounded-3xl p-6 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <div className="flex items-center gap-3.5 mb-3">
          <div className="w-11 h-11 rounded-2xl bg-rose-50 border border-rose-200 flex items-center justify-center text-[#E60039]">
            <span
              className="material-symbols-outlined text-[22px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              spa
            </span>
          </div>
          <div>
            <p className="font-body text-[10px] text-[#7A6E73] uppercase tracking-wider font-bold">
              Current Mood
            </p>
            <p className="font-display text-base font-bold text-[#E60039] mt-0.5">
              {partner.currentMood || 'Feeling Serene'}
            </p>
          </div>
        </div>

        <div className="h-[1px] w-full bg-rose-100 my-3" />

        <div>
          <p className="font-body text-[10px] text-[#7A6E73] uppercase tracking-wider font-bold mb-1.5">
            Status Message
          </p>
          <p className="font-body text-xs text-[#2D2226] leading-relaxed italic bg-rose-50/60 p-3 rounded-2xl border border-rose-100 font-medium">
            "{partner.statusMessage || 'Taking a quiet evening to read and recharge. Can\'t wait to see you tomorrow! ❤️'}"
          </p>
        </div>
      </section>
    </div>
  );
};
