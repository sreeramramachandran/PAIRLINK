import React, { useState } from 'react';
import { ScreenType } from '../types';
import { PairLinkLogo } from './PairLinkLogo';

interface ConnectScreenProps {
  onNavigate: (screen: ScreenType) => void;
  onConnectPartner: (id: string) => void;
}

export const ConnectScreen: React.FC<ConnectScreenProps> = ({
  onNavigate,
  onConnectPartner,
}) => {
  const [targetId, setTargetId] = useState('#PL-8492');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!targetId.trim()) return;
    onConnectPartner(targetId);
    onNavigate('pair_request');
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 relative z-10">
      <main className="w-full max-w-md mx-auto my-auto flex flex-col items-center justify-center py-8">
        {/* Header Text */}
        <div className="w-full text-center mb-6">
          <PairLinkLogo size="lg" showTagline={true} />
          <p className="font-display text-lg font-extrabold text-[#2D2226] mt-3">
            Connect With Your Partner
          </p>
        </div>

        {/* Romantic 3D Heart Graphics */}
        <div className="w-full aspect-square max-w-[220px] mx-auto mb-6 relative rounded-full flex items-center justify-center p-4">
          <div className="w-40 h-40 rounded-full bg-gradient-to-tr from-rose-100 via-pink-50 to-rose-200 border-4 border-white shadow-xl flex items-center justify-center relative overflow-hidden">
            <span
              className="material-symbols-outlined text-[80px] text-[#E60039] animate-pulse"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
          </div>
        </div>

        {/* Pairing Form Card */}
        <form
          onSubmit={handleSubmit}
          className="bg-white/90 backdrop-blur-2xl rounded-3xl w-full p-7 flex flex-col gap-5 border border-white/90 shadow-[0_16px_40px_rgba(230,0,57,0.1),0_4px_16px_rgba(0,0,0,0.04)] modal-animate"
        >
          {/* Input Field */}
          <div className="relative w-full">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] text-[20px]">
              vpn_key
            </span>
            <input
              type="text"
              value={targetId}
              onChange={(e) => setTargetId(e.target.value)}
              placeholder="Enter Partner ID (e.g. #PL-8492)"
              required
              className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 py-3.5 pl-12 pr-4 font-display font-bold text-center text-lg rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
            />
          </div>

          {/* Action Button */}
          <button
            type="submit"
            className="w-full glow-button text-white font-display font-bold text-base py-4 rounded-full flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer"
          >
            <span>Send Connection Request</span>
            <span
              className="material-symbols-outlined text-[20px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
          </button>

          <div className="text-center">
            <button
              type="button"
              onClick={() => alert('Ask your partner to check their PairLink app settings or onboarding screen to view their Partner ID!')}
              className="text-[#7A6E73] hover:text-[#E60039] transition-colors font-body text-xs font-semibold"
            >
              Need help finding your ID?
            </button>
          </div>
        </form>
      </main>
    </div>
  );
};
