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
    <div className="pt-24 pb-32 px-6 max-w-2xl mx-auto flex flex-col gap-5 relative z-10">
      {/* Hero Profile Card */}
      <section className="bg-white/10 backdrop-blur-3xl border-t border-t-white/30 border-x border-x-white/10 border-b border-b-white/5 rounded-[32px] p-8 flex flex-col items-center text-center relative overflow-hidden shadow-[0_8px_32px_0_rgba(0,0,0,0.37)]">
        {/* Decorative blur blob */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-48 h-48 bg-[#ffcbd5]/20 rounded-full blur-[60px] -z-10" />

        <div className="relative mb-5">
          <img
            src={partner.avatarUrl}
            alt={partner.name}
            className="w-32 h-32 rounded-full object-cover border-2 border-white/20 shadow-[0_0_30px_rgba(244,167,185,0.25)]"
          />
        </div>

        <h1 className="font-display font-bold text-2xl md:text-3xl text-[#dae2fd] mb-1">
          {partner.name}
        </h1>
        <p className="font-body text-base text-[#ffcbd5] italic mb-6">
          "{userNickname || partner.nickname || 'My Moon & Stars'}"
        </p>

        <button
          onClick={() => onNavigate('edit_profile')}
          className="bg-[#ffcbd5] text-[#521f2e] font-body text-xs font-bold uppercase tracking-wider px-6 py-3 rounded-full flex items-center gap-2 hover:bg-[#ffd9e0] active:scale-95 transition-all border border-white/20 shadow-[0_0_15px_rgba(244,167,185,0.4)] cursor-pointer"
        >
          <span className="material-symbols-outlined text-[18px]">edit</span>
          Edit Profile
        </button>
      </section>

      {/* Bento Grid Details */}
      <section className="grid grid-cols-2 gap-4">
        {/* Partner ID */}
        <div className="bg-white/10 backdrop-blur-3xl border-t border-t-white/30 border border-white/10 rounded-[24px] p-5 flex flex-col justify-center items-start gap-2 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)]">
          <span className="material-symbols-outlined text-[#d6c1c5] text-[20px]">
            badge
          </span>
          <div>
            <p className="font-body text-xs text-[#d6c1c5] uppercase tracking-wider font-semibold">
              Partner ID
            </p>
            <p className="font-display text-lg font-bold text-[#dae2fd] mt-1">
              {partner.partnerId}
            </p>
          </div>
        </div>

        {/* Birthday */}
        <div className="bg-white/10 backdrop-blur-3xl border-t border-t-white/30 border border-white/10 rounded-[24px] p-5 flex flex-col justify-center items-start gap-2 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)]">
          <span className="material-symbols-outlined text-[#d6c1c5] text-[20px]">
            cake
          </span>
          <div>
            <p className="font-body text-xs text-[#d6c1c5] uppercase tracking-wider font-semibold">
              Birthday
            </p>
            <p className="font-display text-lg font-bold text-[#dae2fd] mt-1">
              Oct 14
            </p>
          </div>
        </div>
      </section>

      {/* Status & Mood Card */}
      <section className="bg-white/10 backdrop-blur-3xl border-t border-t-white/30 border border-white/10 rounded-[24px] p-6 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)]">
        <div className="flex items-center gap-4 mb-4">
          <div className="w-12 h-12 rounded-full bg-[#593d5f]/50 border border-[#dfbbe4]/30 flex items-center justify-center">
            <span
              className="material-symbols-outlined text-[#dfbbe4] text-[24px]"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              spa
            </span>
          </div>
          <div>
            <p className="font-body text-xs text-[#d6c1c5] uppercase tracking-wider font-semibold">
              Current Mood
            </p>
            <p className="font-display text-lg font-semibold text-[#dfbbe4] mt-0.5">
              {partner.currentMood || 'Feeling Serene'}
            </p>
          </div>
        </div>

        <div className="h-[1px] w-full bg-white/10 my-4" />

        <div>
          <p className="font-body text-xs text-[#d6c1c5] uppercase tracking-wider font-semibold mb-2">
            Status Message
          </p>
          <p className="font-body text-sm text-[#dae2fd]/90 leading-relaxed italic">
            "{partner.statusMessage || 'Taking a quiet evening to read and recharge. Can\'t wait to see you tomorrow! ❤️'}"
          </p>
        </div>
      </section>
    </div>
  );
};
