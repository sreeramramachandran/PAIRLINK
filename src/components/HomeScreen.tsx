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

  const handleHeartPress = (e: React.MouseEvent | React.TouchEvent) => {
    setIsPressingHeart(true);
    onSendHeartPulse();

    // Spawn visual floating heart
    const id = Date.now();
    const x = (Math.random() - 0.5) * 80;
    const y = Math.random() * 20;
    setHearts((prev) => [...prev.slice(-12), { id, x, y }]);
    setTimeout(() => setIsPressingHeart(false), 300);
  };

  return (
    <div className="pt-20 pb-28 px-4 sm:px-6 max-w-lg mx-auto min-h-screen flex flex-col gap-4 relative z-10">
      {/* ------------------------------------------------------------- */}
      {/* 1. DISTANCE BANNER CARD ("The Distance Between Us")           */}
      {/* ------------------------------------------------------------- */}
      <div className="relative bg-white/90 backdrop-blur-2xl rounded-3xl border border-white/90 shadow-[0_12px_36px_-6px_rgba(230,0,57,0.08),0_4px_16px_rgba(0,0,0,0.03)] p-5 flex flex-col items-center gap-3 overflow-hidden">
        {/* Top Header Location Indicators */}
        <div className="w-full flex justify-between items-center text-[11px] font-semibold text-[#7A6E73] px-1">
          <div className="flex items-center gap-1">
            <span className="text-[#E60039]">📍</span>
            <span>Indore, India</span>
          </div>
          <div className="flex items-center gap-1">
            <span>Malappuram, India</span>
            <span className="text-[#E60039]">📍</span>
          </div>
        </div>

        {/* Center Distance Metric */}
        <div className="text-center flex flex-col items-center mt-1">
          <p className="text-xs font-semibold text-[#7A6E73] uppercase tracking-wider">
            The Distance Between Us
          </p>
          <div className="flex items-center gap-2 mt-0.5">
            <span className="text-[#E60039] text-xs">✨</span>
            <h2 className="font-display font-extrabold text-3xl text-[#2D2226] tracking-tight">
              1,847 km
            </h2>
            <span className="text-[#E60039] text-xs">✨</span>
          </div>
          <p className="text-xs text-[#7A6E73] font-medium flex items-center gap-1 mt-0.5">
            <span>But never far at heart</span>
            <span className="text-[#E60039]">♡</span>
          </p>
        </div>

        {/* Dual Avatars with Curved Dotted Path & Heart */}
        <div className="w-full flex justify-between items-center px-4 my-2 relative">
          {/* SVG Dotted Curved Line */}
          <div className="absolute inset-x-12 top-1/2 -translate-y-1/2 h-12 pointer-events-none z-0">
            <svg viewBox="0 0 200 40" fill="none" className="w-full h-full">
              <path
                d="M 10 20 Q 100 38 190 20"
                stroke="#FF809B"
                strokeWidth="2"
                strokeDasharray="4 4"
              />
            </svg>
            {/* Center Heart on Path */}
            <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-7 h-7 rounded-full bg-white shadow-md border border-rose-100 flex items-center justify-center text-[#E60039] text-xs animate-pulse">
              ❤️
            </div>
          </div>

          {/* Left Boy Avatar */}
          <div className="relative z-10 flex flex-col items-center">
            <div className="w-16 h-16 rounded-full overflow-hidden border-2 border-rose-300 shadow-md p-0.5 bg-white relative">
              <img
                src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80"
                alt="User Avatar"
                className="w-full h-full rounded-full object-cover"
              />
              <span className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-3 h-3 bg-[#E60039] rounded-full border-2 border-white" />
            </div>
          </div>

          {/* Right Girl Avatar */}
          <div className="relative z-10 flex flex-col items-center">
            <div className="w-16 h-16 rounded-full overflow-hidden border-2 border-rose-300 shadow-md p-0.5 bg-white relative">
              <img
                src={partner.avatarUrl}
                alt={partner.name}
                className="w-full h-full rounded-full object-cover"
              />
              <span className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-3 h-3 bg-[#E60039] rounded-full border-2 border-white" />
            </div>
          </div>
        </div>

        {/* Send Love Button */}
        <div className="relative flex flex-col items-center mt-1">
          {/* Spawning Hearts */}
          {hearts.map((h) => (
            <div
              key={h.id}
              className="absolute -top-8 pointer-events-none animate-bounce text-[#E60039] font-bold text-xl transition-all duration-700"
              style={{ transform: `translate(${h.x}px, -30px)` }}
            >
              ❤️
            </div>
          ))}

          <button
            onClick={handleHeartPress}
            className={`glow-button px-7 py-2.5 rounded-full font-display font-bold text-sm flex items-center gap-2 tracking-wide cursor-pointer transition-all active:scale-95 ${
              isPressingHeart ? 'scale-105 shadow-[0_0_25px_rgba(230,0,57,0.6)]' : ''
            }`}
          >
            <span>Send Love</span>
            <span className="text-base">❤️</span>
          </button>
          {heartPulseCount > 0 && (
            <span className="text-[10px] font-semibold text-[#7A6E73] mt-1">
              {heartPulseCount} love bursts sent today
            </span>
          )}
        </div>
      </div>

      {/* ------------------------------------------------------------- */}
      {/* 2. COUNTDOWN CARD ("NEXT TIME TOGETHER")                      */}
      {/* ------------------------------------------------------------- */}
      <div
        onClick={() => onNavigate('birthday')}
        className="relative bg-gradient-to-r from-[#FFF5F7] via-[#FFF0F3] to-[#FFE6EC] rounded-3xl border border-rose-200/80 shadow-[0_8px_24px_rgba(230,0,57,0.06)] p-5 flex items-center justify-between cursor-pointer hover:shadow-md transition-all overflow-hidden group"
      >
        {/* Airplane Watermark Background Pattern */}
        <div className="absolute right-2 bottom-0 opacity-15 pointer-events-none">
          <span className="material-symbols-outlined text-[90px] text-[#E60039]">
            flight_takeoff
          </span>
        </div>

        {/* Left Side: Countdown Timer */}
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-1.5 text-[11px] font-bold text-[#7A6E73] tracking-wider uppercase">
            <span className="text-[#E60039]">✈️</span>
            <span>Next Time Together</span>
            <span className="text-[#E60039]">✈️</span>
          </div>

          <div className="flex items-center gap-3 text-center">
            <div>
              <span className="font-display font-extrabold text-2xl text-[#2D2226] block leading-none">
                27
              </span>
              <span className="text-[9px] font-bold text-[#7A6E73] tracking-wider uppercase mt-1 block">
                Days
              </span>
            </div>
            <span className="text-[#7A6E73]/40 font-bold text-lg leading-none">:</span>
            <div>
              <span className="font-display font-extrabold text-2xl text-[#2D2226] block leading-none">
                14
              </span>
              <span className="text-[9px] font-bold text-[#7A6E73] tracking-wider uppercase mt-1 block">
                Hours
              </span>
            </div>
            <span className="text-[#7A6E73]/40 font-bold text-lg leading-none">:</span>
            <div>
              <span className="font-display font-extrabold text-2xl text-[#2D2226] block leading-none">
                32
              </span>
              <span className="text-[9px] font-bold text-[#7A6E73] tracking-wider uppercase mt-1 block">
                Minutes
              </span>
            </div>
            <span className="text-[#7A6E73]/40 font-bold text-lg leading-none">:</span>
            <div>
              <span className="font-display font-extrabold text-2xl text-[#2D2226] block leading-none">
                59
              </span>
              <span className="text-[9px] font-bold text-[#7A6E73] tracking-wider uppercase mt-1 block">
                Seconds
              </span>
            </div>
          </div>
        </div>

        {/* Vertical Divider */}
        <div className="h-12 w-[1px] bg-rose-200/80 mx-2" />

        {/* Right Side: Meeting Date Detail */}
        <div className="flex flex-col items-end text-right z-10">
          <span className="text-xs font-semibold text-[#7A6E73] flex items-center gap-1">
            <span>Our next meeting</span>
            <span className="text-[#E60039]">❤️</span>
          </span>
          <span className="font-display font-extrabold text-lg text-[#E60039] mt-0.5">
            24 Dec 2026
          </span>
          <span className="text-[11px] font-medium text-[#7A6E73] mt-0.5">
            We can't wait!
          </span>
        </div>
      </div>

      {/* ------------------------------------------------------------- */}
      {/* 3. 5 QUICK ACTION GRID CARDS (3D Styled Icons)                */}
      {/* ------------------------------------------------------------- */}
      <div className="grid grid-cols-5 gap-2.5 sm:gap-3">
        {/* 1. Chat */}
        <button
          onClick={() => onNavigate('updates')}
          className="bg-white/90 border border-white/90 shadow-[0_8px_20px_rgba(230,0,57,0.06)] rounded-2xl p-2.5 sm:p-3 flex flex-col items-center text-center group hover:translate-y-[-2px] transition-all cursor-pointer"
        >
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-rose-50 to-pink-100 border border-rose-200/60 flex items-center justify-center text-[#E60039] shadow-sm group-hover:scale-105 transition-transform">
            <span
              className="material-symbols-outlined text-[22px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              chat_bubble
            </span>
          </div>
          <span className="font-display font-bold text-xs text-[#2D2226] mt-2 leading-tight">
            Chat
          </span>
          <span className="text-[9px] text-[#7A6E73] font-medium mt-0.5 line-clamp-1">
            Talk anytime
          </span>
        </button>

        {/* 2. Memories */}
        <button
          onClick={() => onNavigate('birthday')}
          className="bg-white/90 border border-white/90 shadow-[0_8px_20px_rgba(230,0,57,0.06)] rounded-2xl p-2.5 sm:p-3 flex flex-col items-center text-center group hover:translate-y-[-2px] transition-all cursor-pointer"
        >
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-rose-50 to-pink-100 border border-rose-200/60 flex items-center justify-center text-[#E60039] shadow-sm group-hover:scale-105 transition-transform">
            <span
              className="material-symbols-outlined text-[22px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              photo_library
            </span>
          </div>
          <span className="font-display font-bold text-xs text-[#2D2226] mt-2 leading-tight">
            Memories
          </span>
          <span className="text-[9px] text-[#7A6E73] font-medium mt-0.5 line-clamp-1">
            Our moments
          </span>
        </button>

        {/* 3. Surprise */}
        <button
          onClick={() => onNavigate('mood')}
          className="bg-white/90 border border-white/90 shadow-[0_8px_20px_rgba(230,0,57,0.06)] rounded-2xl p-2.5 sm:p-3 flex flex-col items-center text-center group hover:translate-y-[-2px] transition-all cursor-pointer"
        >
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-rose-50 to-pink-100 border border-rose-200/60 flex items-center justify-center text-[#E60039] shadow-sm group-hover:scale-105 transition-transform">
            <span
              className="material-symbols-outlined text-[22px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              card_giftcard
            </span>
          </div>
          <span className="font-display font-bold text-xs text-[#2D2226] mt-2 leading-tight">
            Surprise
          </span>
          <span className="text-[9px] text-[#7A6E73] font-medium mt-0.5 line-clamp-1">
            Make them smile
          </span>
        </button>

        {/* 4. Location */}
        <button
          onClick={() => onNavigate('update_status')}
          className="bg-white/90 border border-white/90 shadow-[0_8px_20px_rgba(230,0,57,0.06)] rounded-2xl p-2.5 sm:p-3 flex flex-col items-center text-center group hover:translate-y-[-2px] transition-all cursor-pointer"
        >
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-rose-50 to-pink-100 border border-rose-200/60 flex items-center justify-center text-[#E60039] shadow-sm group-hover:scale-105 transition-transform">
            <span
              className="material-symbols-outlined text-[22px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              location_on
            </span>
          </div>
          <span className="font-display font-bold text-xs text-[#2D2226] mt-2 leading-tight">
            Location
          </span>
          <span className="text-[9px] text-[#7A6E73] font-medium mt-0.5 line-clamp-1">
            See each other
          </span>
        </button>

        {/* 5. Love Notes */}
        <button
          onClick={() => onNavigate('updates')}
          className="bg-white/90 border border-white/90 shadow-[0_8px_20px_rgba(230,0,57,0.06)] rounded-2xl p-2.5 sm:p-3 flex flex-col items-center text-center group hover:translate-y-[-2px] transition-all cursor-pointer"
        >
          <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-rose-50 to-pink-100 border border-rose-200/60 flex items-center justify-center text-[#E60039] shadow-sm group-hover:scale-105 transition-transform">
            <span
              className="material-symbols-outlined text-[22px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
          </div>
          <span className="font-display font-bold text-xs text-[#2D2226] mt-2 leading-tight">
            Love Notes
          </span>
          <span className="text-[9px] text-[#7A6E73] font-medium mt-0.5 line-clamp-1">
            Notes from heart
          </span>
        </button>
      </div>

      {/* ------------------------------------------------------------- */}
      {/* 4. TODAY'S CONNECTION CARD                                    */}
      {/* ------------------------------------------------------------- */}
      <div className="bg-white/90 border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)] rounded-3xl p-5 flex flex-col gap-3 relative overflow-hidden">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            {/* 3D Glass Dome Heart Graphic */}
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-rose-100 via-pink-50 to-rose-200 border border-rose-200 flex items-center justify-center shadow-inner relative overflow-hidden">
              <span
                className="material-symbols-outlined text-[32px] text-[#E60039] animate-pulse"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                favorite
              </span>
              <div className="absolute inset-0 bg-gradient-to-t from-white/40 to-transparent pointer-events-none" />
            </div>

            <div className="flex flex-col">
              <div className="flex items-center gap-1 text-[11px] font-bold text-[#E60039] tracking-wider uppercase">
                <span>Today's Connection</span>
                <span>💬</span>
              </div>
              <h3 className="font-display font-bold text-sm text-[#2D2226] mt-0.5">
                What is one thing you wish we could do together today?
              </h3>
              <p className="text-[11px] text-[#7A6E73] font-medium mt-0.5">
                Answer together and unlock each other's answers.
              </p>
            </div>
          </div>

          {/* Answer Now Button */}
          <button
            onClick={() => onNavigate('mood')}
            className="glow-button px-4 py-2 rounded-full font-display font-bold text-xs flex items-center gap-1 whitespace-nowrap cursor-pointer hover:scale-105 active:scale-95 transition-all"
          >
            <span>Answer Now</span>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
          </button>
        </div>

        {/* Bottom Status Pill */}
        <div className="flex items-center justify-between bg-rose-50/70 border border-rose-100/80 rounded-full px-3.5 py-1.5 mt-1">
          <div className="flex items-center gap-2">
            <div className="flex -space-x-1.5">
              <img
                src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80"
                className="w-5 h-5 rounded-full border border-white object-cover"
                alt="User"
              />
              <img
                src={partner.avatarUrl}
                className="w-5 h-5 rounded-full border border-white object-cover"
                alt="Partner"
              />
            </div>
            <span className="text-[11px] font-semibold text-[#7A6E73]">
              Both of you haven't answered yet
            </span>
          </div>
          <span className="material-symbols-outlined text-[14px] text-[#7A6E73]">lock</span>
        </div>
      </div>

      {/* ------------------------------------------------------------- */}
      {/* 5. BOTTOM ROW GRID (Note Card + Weather & Time Card)          */}
      {/* ------------------------------------------------------------- */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {/* Left Note Card */}
        <div className="bg-gradient-to-br from-rose-50/90 via-white to-pink-50/90 border border-rose-200/70 shadow-[0_8px_24px_rgba(230,0,57,0.06)] rounded-3xl p-4 flex flex-col justify-between gap-3">
          <div className="flex flex-col gap-1.5">
            <div className="flex items-center gap-2">
              <img
                src={partner.avatarUrl}
                alt={partner.name}
                className="w-6 h-6 rounded-full object-cover border border-rose-300"
              />
              <span className="text-xs font-bold text-[#E60039]">
                {partner.name} sent you a note 💌
              </span>
            </div>
            <p className="text-xs italic font-medium text-[#2D2226] bg-white/80 border border-rose-100 rounded-2xl p-3 shadow-xs mt-1">
              "Thinking of you a little extra today... miss you always! ❤️"
            </p>
          </div>

          <button
            onClick={() => onNavigate('updates')}
            className="w-full bg-white hover:bg-rose-50 text-[#E60039] border border-rose-200 font-display font-bold text-xs py-2 rounded-full shadow-xs transition-colors cursor-pointer text-center"
          >
            Read Now
          </button>
        </div>

        {/* Right Weather & Time Dual Card */}
        <div className="bg-white/90 border border-white/90 shadow-[0_8px_24px_rgba(230,0,57,0.06)] rounded-3xl p-4 flex flex-col justify-between gap-2">
          {/* Location & Temperature */}
          <div className="flex justify-between items-start">
            <div>
              <div className="flex items-center gap-1 text-[11px] font-bold text-[#7A6E73]">
                <span className="text-[#E60039]">📍</span>
                <span>Malappuram</span>
              </div>
              <div className="flex items-baseline gap-1.5 mt-1">
                <span className="font-display font-extrabold text-3xl text-[#2D2226]">
                  28°
                </span>
                <span className="text-xs font-semibold text-[#7A6E73]">
                  Partly Cloudy
                </span>
              </div>
            </div>
            <span className="text-2xl">🌤️</span>
          </div>

          {/* Dual Clocks */}
          <div className="bg-rose-50/60 border border-rose-100 rounded-2xl p-2.5 flex items-center justify-between text-center mt-2">
            <div>
              <span className="text-[9px] font-bold text-[#7A6E73] uppercase tracking-wider block">
                Your Time
              </span>
              <span className="font-display font-bold text-xs text-[#2D2226] block mt-0.5">
                9:41 PM
              </span>
            </div>

            <div className="flex items-center gap-1 text-[#E60039] text-xs">
              <span>--</span>
              <span>❤️</span>
              <span>--</span>
            </div>

            <div>
              <span className="text-[9px] font-bold text-[#7A6E73] uppercase tracking-wider block">
                Chakkara's Time
              </span>
              <span className="font-display font-bold text-xs text-[#2D2226] block mt-0.5">
                9:41 PM
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
