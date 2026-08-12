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
    <header className="fixed top-0 left-0 w-full z-40 backdrop-blur-2xl bg-white/10 border-b border-white/20 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] transition-all duration-300">
      <div className="max-w-2xl mx-auto flex justify-between items-center px-6 py-3.5">
        {/* Leading: Back Button or Partner Avatar */}
        <div className="flex items-center gap-3">
          {showBack ? (
            <button
              onClick={onBack}
              className="text-[#dae2fd] hover:text-[#ffcbd5] transition-colors p-1.5 rounded-full hover:bg-white/10 active:scale-95"
              aria-label="Go back"
            >
              <span className="material-symbols-outlined text-[24px]">arrow_back</span>
            </button>
          ) : (
            <button
              onClick={() => onNavigate('profile')}
              className="relative flex items-center justify-center group cursor-pointer"
              title="View Profile"
            >
              <div className="w-10 h-10 rounded-full overflow-hidden border border-white/30 shadow-[0_0_12px_rgba(255,203,213,0.3)] group-hover:border-[#ffcbd5] transition-colors">
                <img
                  src={partnerAvatar}
                  alt={partnerName}
                  className="w-full h-full object-cover"
                />
              </div>
              {isPartnerOnline && (
                <div
                  className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-emerald-400 rounded-full border-2 border-[#0b1326] shadow-[0_0_8px_rgba(52,211,153,0.8)]"
                  title="Online"
                />
              )}
            </button>
          )}

          <h1 className="font-display font-bold text-[20px] text-[#ffcbd5] tracking-tight">
            {titleOverride || 'PairLink'}
          </h1>
        </div>

        {/* Trailing: Heart button with animation */}
        <button
          onClick={onSendHeart}
          className="text-[#ffcbd5] hover:opacity-80 transition-all active:scale-90 p-2 rounded-full hover:bg-white/10 relative group"
          title="Send Love Burst"
        >
          <span
            className="material-symbols-outlined text-[24px]"
            data-weight="fill"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            favorite
          </span>
          <span className="absolute -bottom-8 right-0 bg-white/20 backdrop-blur-md text-[10px] text-[#ffcbd5] px-2 py-0.5 rounded-full opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap pointer-events-none border border-white/10">
            Send Heart
          </span>
        </button>
      </div>
    </header>
  );
};
