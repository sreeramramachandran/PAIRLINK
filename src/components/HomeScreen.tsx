import React, { useState } from 'react';
import { ScreenType, PartnerProfile } from '../types';

interface HomeScreenProps {
  partner: PartnerProfile;
  relationshipStartDate: string;
  onNavigate: (screen: ScreenType) => void;
  onSendHeartPulse: () => void;
  heartPulseCount: number;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  partner,
  relationshipStartDate,
  onNavigate,
  onSendHeartPulse,
  heartPulseCount,
}) => {
  const [isPressingHeart, setIsPressingHeart] = useState(false);
  const [hearts, setHearts] = useState<{ id: number; x: number; y: number }[]>([]);

  const calculateTogetherTime = (startDateStr: string) => {
    try {
      const start = new Date(startDateStr);
      const now = new Date();
      let years = now.getFullYear() - start.getFullYear();
      let months = now.getMonth() - start.getMonth();
      if (months < 0) {
        years--;
        months += 12;
      }
      return `${years > 0 ? years + 'Y ' : ''}${months}M`;
    } catch {
      return '3Y 2M';
    }
  };

  const calculateDaysToBirthday = (dobStr: string) => {
    try {
      const today = new Date();
      const dob = new Date(dobStr);
      const nextBirthday = new Date(
        today.getFullYear(),
        dob.getMonth(),
        dob.getDate()
      );
      if (nextBirthday < today) {
        nextBirthday.setFullYear(today.getFullYear() + 1);
      }
      const diffTime = nextBirthday.getTime() - today.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return `${diffDays} Days`;
    } catch {
      return '42 Days';
    }
  };

  const handleHeartPressStart = (e: React.MouseEvent | React.TouchEvent) => {
    setIsPressingHeart(true);
    onSendHeartPulse();

    // Spawn visual floating heart
    const id = Date.now();
    const x = Math.random() * 60 - 30; // random offset
    const y = Math.random() * 20;
    setHearts((prev) => [...prev.slice(-10), { id, x, y }]);
  };

  const handleHeartPressEnd = () => {
    setIsPressingHeart(false);
  };

  return (
    <div className="pt-24 pb-32 px-6 max-w-md mx-auto min-h-screen flex flex-col items-center justify-center relative z-10">
      {/* Large Frosted Glass Card */}
      <div className="relative w-full bg-white/10 backdrop-blur-3xl rounded-[32px] border border-white/20 shadow-[0_16px_48px_rgba(0,0,0,0.4)] p-8 flex flex-col items-center gap-4 overflow-hidden">
        {/* Top Edge Highlight */}
        <div className="absolute inset-0 rounded-[32px] border-t border-white/30 pointer-events-none" />

        {/* Partner Avatar */}
        <div className="relative w-32 h-32 mt-2">
          <div className="w-full h-full rounded-full overflow-hidden border-2 border-emerald-400 shadow-[0_0_24px_rgba(52,211,153,0.35)]">
            <img
              src={partner.avatarUrl}
              alt={partner.name}
              className="w-full h-full object-cover"
            />
          </div>
          {/* Mood Badge */}
          <button
            onClick={() => onNavigate('mood')}
            className="absolute bottom-1 right-1 flex items-center justify-center w-9 h-9 rounded-full bg-white/20 border border-white/30 shadow-lg backdrop-blur-md hover:bg-white/30 transition-all cursor-pointer"
            title="Update Mood"
          >
            <span
              className="material-symbols-outlined text-[#ffcbd5] text-[18px]"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              mood
            </span>
          </button>
        </div>

        {/* Greeting & Online Status */}
        <div className="text-center flex flex-col gap-1.5 mt-1">
          <h2 className="font-display font-bold text-2xl md:text-3xl text-[#dae2fd]">
            Hi, {partner.name}
          </h2>
          <div className="flex items-center justify-center gap-2">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 shadow-[0_0_8px_rgba(52,211,153,1)]" />
            <p className="font-display text-sm text-[#d6c1c5] font-medium tracking-wide">
              {partner.online ? 'Online' : 'Away'}
            </p>
          </div>
        </div>

        {/* Pulse Heart Button */}
        <div className="flex flex-col items-center gap-3 mt-4 mb-2 relative">
          {/* Floating Hearts Animation */}
          {hearts.map((h) => (
            <div
              key={h.id}
              className="absolute -top-12 pointer-events-none animate-bounce text-[#ffcbd5] font-bold text-2xl transition-all duration-1000 opacity-0 transform -translate-y-12"
              style={{ transform: `translate(${h.x}px, -40px)` }}
            >
              ❤️
            </div>
          ))}

          <button
            onMouseDown={handleHeartPressStart}
            onMouseUp={handleHeartPressEnd}
            onTouchStart={handleHeartPressStart}
            onTouchEnd={handleHeartPressEnd}
            className={`w-24 h-24 rounded-full bg-[#ffcbd5] flex items-center justify-center text-[#521f2e] shadow-lg animate-heart-pulse hover:opacity-95 active:scale-95 transition-all duration-300 relative group cursor-pointer ${
              isPressingHeart ? 'scale-110 shadow-[0_0_40px_rgba(244,167,185,1)]' : ''
            }`}
            title="Press & Hold to send love burst"
          >
            <span
              className="material-symbols-outlined text-[42px]"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
            <div className="absolute inset-0 rounded-full border border-white/40 pointer-events-none mix-blend-overlay" />
          </button>

          <div className="flex flex-col items-center">
            <span className="text-xs font-body text-[#d6c1c5]/80 tracking-widest uppercase font-semibold">
              Press & Hold
            </span>
            {heartPulseCount > 0 && (
              <span className="text-[11px] text-[#ffcbd5] font-body mt-0.5 font-medium">
                {heartPulseCount} heartbeats sent today
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Grid of Bento Features */}
      <div className="w-full max-w-md grid grid-cols-2 gap-4 mt-6">
        {/* Mood Card */}
        <button
          onClick={() => onNavigate('mood')}
          className="bg-white/10 backdrop-blur-2xl rounded-2xl border border-white/20 p-4 flex flex-col items-center justify-center gap-2 shadow-lg hover:bg-white/15 transition-all active:scale-98 text-left cursor-pointer"
        >
          <div className="w-10 h-10 rounded-full bg-[#ffcbd5]/20 flex items-center justify-center">
            <span className="material-symbols-outlined text-[#ffcbd5]">
              mood
            </span>
          </div>
          <div className="text-center">
            <p className="font-body text-[11px] text-[#d6c1c5] uppercase tracking-wider font-semibold">
              Current Mood
            </p>
            <p className="font-display text-[15px] text-[#dae2fd] font-medium mt-0.5">
              {partner.currentMood}
            </p>
          </div>
        </button>

        {/* Status Card */}
        <button
          onClick={() => onNavigate('update_status')}
          className="bg-white/10 backdrop-blur-2xl rounded-2xl border border-white/20 p-4 flex flex-col items-center justify-center gap-2 shadow-lg hover:bg-white/15 transition-all active:scale-98 text-left cursor-pointer"
        >
          <div className="w-10 h-10 rounded-full bg-[#ffa3ab]/20 flex items-center justify-center">
            <span className="material-symbols-outlined text-[#ffa3ab]">
              work
            </span>
          </div>
          <div className="text-center">
            <p className="font-body text-[11px] text-[#d6c1c5] uppercase tracking-wider font-semibold">
              Current Status
            </p>
            <p className="font-display text-[15px] text-[#dae2fd] font-medium mt-0.5">
              {partner.currentStatus}
            </p>
          </div>
        </button>

        {/* Together For Card */}
        <button
          onClick={() => onNavigate('relationship_setup')}
          className="bg-white/10 backdrop-blur-2xl rounded-2xl border border-white/20 p-4 flex flex-col items-center justify-center gap-2 shadow-lg hover:bg-white/15 transition-all active:scale-98 text-left cursor-pointer"
        >
          <div className="w-10 h-10 rounded-full bg-[#dfbbe4]/20 flex items-center justify-center">
            <span className="material-symbols-outlined text-[#dfbbe4]">
              favorite
            </span>
          </div>
          <div className="text-center">
            <p className="font-body text-[11px] text-[#d6c1c5] uppercase tracking-wider font-semibold">
              Together For
            </p>
            <p className="font-display text-[15px] text-[#dae2fd] font-medium mt-0.5">
              {calculateTogetherTime(relationshipStartDate)}
            </p>
          </div>
        </button>

        {/* Birthday Countdown Card */}
        <button
          onClick={() => onNavigate('birthday')}
          className="bg-white/10 backdrop-blur-2xl rounded-2xl border border-white/20 p-4 flex flex-col items-center justify-center gap-2 shadow-lg hover:bg-white/15 transition-all active:scale-98 text-left cursor-pointer"
        >
          <div className="w-10 h-10 rounded-full bg-sky-400/20 flex items-center justify-center">
            <span className="material-symbols-outlined text-sky-300">
              cake
            </span>
          </div>
          <div className="text-center">
            <p className="font-body text-[11px] text-[#d6c1c5] uppercase tracking-wider font-semibold">
              Birthday In
            </p>
            <p className="font-display text-[15px] text-[#dae2fd] font-medium mt-0.5">
              {calculateDaysToBirthday(partner.dob)}
            </p>
          </div>
        </button>
      </div>
    </div>
  );
};
