import React from 'react';
import { ScreenType } from '../types';

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
      <main className="relative z-10 w-full max-w-md mx-auto flex flex-col items-center justify-center my-auto space-y-10 py-10">
        {/* Header Text */}
        <div className="text-center space-y-2">
          <h1 className="font-display font-bold text-4xl md:text-5xl bg-gradient-to-r from-[#ffcbd5] to-[#dfbbe4] bg-clip-text text-transparent drop-shadow-[0_0_20px_rgba(244,167,185,0.4)]">
            Connected Forever
          </h1>
          <p className="font-body text-lg text-[#d6c1c5]">
            Your sanctuary is ready.
          </p>
        </div>

        {/* Profile Avatars Group */}
        <div className="relative flex items-center justify-center h-60 w-full">
          {/* Connection Line / Heart */}
          <div className="absolute z-20 flex items-center justify-center animate-pulse">
            <div className="glass-card rounded-full p-4 flex items-center justify-center bg-white/10 border border-white/20 shadow-[0_0_25px_rgba(244,167,185,0.6)]">
              <span
                className="material-symbols-outlined text-[#ffcbd5] text-4xl"
                data-weight="fill"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                favorite
              </span>
            </div>
          </div>

          {/* Partner Avatar (Left) */}
          <div className="absolute left-8 z-10 w-32 h-32 rounded-full overflow-hidden border-2 border-white/40 glow-aura transform -rotate-6 transition-all duration-700">
            <img
              src={partnerAvatar}
              alt="Partner"
              className="w-full h-full object-cover"
            />
          </div>

          {/* User Avatar (Right) */}
          <div className="absolute right-8 z-30 w-32 h-32 rounded-full overflow-hidden border-2 border-white/40 glow-aura transform rotate-6 transition-all duration-700">
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
            className="w-full glass-card rounded-full py-4 px-6 flex items-center justify-center space-x-2 text-[#ffcbd5] font-display font-medium text-lg hover:bg-white/20 transition-all duration-300 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] group active:scale-95 border border-white/20"
          >
            <span className="font-bold">Continue</span>
            <span className="material-symbols-outlined text-[#ffcbd5] group-hover:translate-x-1 transition-transform">
              arrow_forward
            </span>
          </button>
        </div>
      </main>
    </div>
  );
};
