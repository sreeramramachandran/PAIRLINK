import React from 'react';
import { ScreenType } from '../types';
import { PairLinkLogo } from './PairLinkLogo';

interface ConnectedForeverScreenProps {
  userAvatar: string;
  partnerAvatar: string;
  onNavigate: (screen: ScreenType) => void;
}

export const ConnectedForeverScreen: React.FC<ConnectedForeverScreenProps> = ({
  userAvatar,
  partnerAvatar,
  onNavigate,
}) => {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 relative z-10">
      <main className="relative z-10 w-full max-w-md mx-auto flex flex-col items-center justify-center my-auto space-y-8 py-10">
        {/* Header Text */}
        <div className="text-center space-y-3">
          <PairLinkLogo size="lg" showTagline={true} />
          <h1 className="font-display font-extrabold text-3xl text-[#2D2226] mt-4">
            Connected Forever ❤️
          </h1>
          <p className="font-body text-sm text-[#7A6E73] font-medium">
            Your digital sanctuary is now ready.
          </p>
        </div>

        {/* Profile Avatars Group */}
        <div className="relative flex items-center justify-center h-52 w-full my-4">
          {/* Center Connection Heart */}
          <div className="absolute z-20 flex items-center justify-center animate-pulse">
            <div className="w-14 h-14 rounded-full bg-[#E60039] text-white flex items-center justify-center shadow-lg shadow-red-500/40 border-4 border-white">
              <span
                className="material-symbols-outlined text-[28px]"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                favorite
              </span>
            </div>
          </div>

          {/* Partner Avatar (Left) */}
          <div className="absolute left-8 z-10 w-28 h-28 rounded-full overflow-hidden border-4 border-white shadow-xl transform -rotate-6 transition-all">
            <img
              src={partnerAvatar}
              alt="Partner"
              className="w-full h-full object-cover"
            />
          </div>

          {/* User Avatar (Right) */}
          <div className="absolute right-8 z-30 w-28 h-28 rounded-full overflow-hidden border-4 border-white shadow-xl transform rotate-6 transition-all">
            <img
              src={userAvatar}
              alt="User"
              className="w-full h-full object-cover"
            />
          </div>
        </div>

        {/* Action Button */}
        <div className="w-full pt-4">
          <button
            onClick={() => onNavigate('relationship_setup')}
            className="w-full glow-button text-white font-display font-bold text-base py-4 rounded-full flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer"
          >
            <span>Continue to Setup</span>
            <span className="material-symbols-outlined text-[20px]">
              arrow_forward
            </span>
          </button>
        </div>
      </main>
    </div>
  );
};
