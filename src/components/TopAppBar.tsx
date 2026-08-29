import React from 'react';
import { ScreenType } from '../types';

interface TopAppBarProps {
  currentScreen: ScreenType;
  onNavigate: (screen: ScreenType) => void;
  partnerAvatar: string;
  partnerName: string;
  isPartnerOnline: boolean;
  onSendHeart?: () => void;
  showBack?: boolean;
  onBack?: () => void;
  titleOverride?: string;
}

export const TopAppBar: React.FC<TopAppBarProps> = ({
  currentScreen,
  onNavigate,
  partnerAvatar,
  partnerName,
  isPartnerOnline,
  onSendHeart,
  showBack,
  onBack,
  titleOverride,
}) => {
  return (
    <header className="fixed top-0 left-0 w-full z-40 backdrop-blur-2xl bg-white/70 border-b border-white/80 shadow-[0_4px_20px_rgba(230,0,57,0.04)] transition-all duration-300">
      <div className="max-w-2xl mx-auto flex justify-between items-center px-5 py-3">
        {/* Leading: Back Button or Greeting Header */}
        <div className="flex items-center gap-3">
          {showBack ? (
            <div className="flex items-center gap-3">
              <button
                onClick={onBack}
                className="text-[#2D2226] hover:text-[#E60039] transition-colors p-2 rounded-full bg-white shadow-md border border-white/90 active:scale-95 flex items-center justify-center"
                aria-label="Go back"
              >
                <span className="material-symbols-outlined text-[22px]">arrow_back</span>
              </button>
              <h1 className="font-display font-extrabold text-[20px] text-[#2D2226] tracking-tight">
                {titleOverride || 'PairLink'}
              </h1>
            </div>
          ) : (
            <div className="flex flex-col">
              <div className="flex items-center gap-1.5 text-xs font-semibold text-[#E60039]">
                <span>Good Evening,</span>
                <span className="text-sm">👋</span>
              </div>
              <h1 className="font-display font-extrabold text-[19px] sm:text-[21px] text-[#2D2226] tracking-tight leading-tight flex items-center gap-1.5">
                <span>{titleOverride || `${partnerName} & Chakkara`}</span>
                <span className="text-[#E60039] text-base">❤️</span>
              </h1>
              <p className="text-[11px] text-[#7A6E73] font-medium hidden sm:block">
                Miles apart, but always close at heart. ❤️
              </p>
            </div>
          )}
        </div>

        {/* Trailing: Action Icons matching Reference (Notifications, Calendar, Avatar) */}
        <div className="flex items-center gap-2.5">
          {/* Notification Bell Button */}
          <button
            onClick={() => onNavigate('updates')}
            className="relative w-10 h-10 rounded-full bg-white shadow-sm border border-white/90 flex items-center justify-center text-[#4A3E43] hover:text-[#E60039] hover:shadow-md transition-all active:scale-95"
            title="Notifications & Updates"
          >
            <span className="material-symbols-outlined text-[20px]">notifications</span>
            <span className="absolute top-2 right-2 w-2 h-2 bg-[#E60039] rounded-full ring-2 ring-white" />
          </button>

          {/* Calendar Button */}
          <button
            onClick={() => onNavigate('birthday')}
            className="w-10 h-10 rounded-full bg-white shadow-sm border border-white/90 flex items-center justify-center text-[#4A3E43] hover:text-[#E60039] hover:shadow-md transition-all active:scale-95"
            title="Special Dates & Wishlist"
          >
            <span className="material-symbols-outlined text-[20px]">calendar_month</span>
          </button>

          {/* Profile Avatar with Online Ring */}
          <button
            onClick={() => onNavigate('profile')}
            className="relative flex items-center justify-center cursor-pointer group active:scale-95 transition-transform ml-1"
            title="View Profile"
          >
            <div className="w-10 h-10 rounded-full overflow-hidden border-2 border-white shadow-md group-hover:border-[#E60039] transition-colors">
              <img
                src={partnerAvatar}
                alt={partnerName}
                className="w-full h-full object-cover"
              />
            </div>
            {isPartnerOnline && (
              <div
                className="absolute bottom-0 right-0 w-3 h-3 bg-emerald-500 rounded-full border-2 border-white shadow-sm"
                title="Online"
              />
            )}
          </button>
        </div>
      </div>
    </header>
  );
};
