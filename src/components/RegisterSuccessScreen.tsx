import React, { useState } from 'react';
import { ScreenType } from '../types';
import { PairLinkLogo } from './PairLinkLogo';

interface RegisterSuccessScreenProps {
  username: string;
  partnerId: string;
  onNavigate: (screen: ScreenType) => void;
}

export const RegisterSuccessScreen: React.FC<RegisterSuccessScreenProps> = ({
  username,
  partnerId,
  onNavigate,
}) => {
  const [copied, setCopied] = useState(false);

  const copyId = () => {
    navigator.clipboard.writeText(partnerId).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 relative z-10">
      <main className="relative z-10 flex-1 flex flex-col items-center justify-center w-full max-w-md mx-auto py-12">
        {/* Logo */}
        <div className="mb-6">
          <PairLinkLogo size="lg" showTagline={true} />
        </div>

        {/* Greeting */}
        <h1 className="font-display font-extrabold text-2xl sm:text-3xl text-center mb-6 text-[#2D2226]">
          Welcome, <span className="text-[#E60039]">{username || 'Alex'}</span>!
        </h1>

        {/* Partner ID Glass Card */}
        <div className="w-full bg-white/90 backdrop-blur-2xl border border-white/90 rounded-3xl p-7 flex flex-col items-center gap-5 shadow-[0_16px_40px_rgba(230,0,57,0.1),0_4px_16px_rgba(0,0,0,0.04)] mb-8 text-center">
          <span className="font-body text-xs text-[#7A6E73] uppercase tracking-wider font-bold">
            Your Unique Partner ID
          </span>

          <div className="flex items-center justify-center gap-3 w-full bg-rose-50/70 border border-rose-200/80 rounded-2xl py-3 px-5">
            <span className="font-display text-xl sm:text-2xl text-[#E60039] tracking-wider font-extrabold">
              {partnerId}
            </span>

            {/* Copy Button */}
            <button
              onClick={copyId}
              aria-label="Copy Partner ID"
              className={`w-10 h-10 flex items-center justify-center rounded-full border transition-all duration-300 active:scale-95 cursor-pointer ${
                copied
                  ? 'bg-emerald-500 text-white border-emerald-500 shadow-md'
                  : 'bg-white text-[#E60039] border-rose-200 hover:bg-rose-50 shadow-sm'
              }`}
            >
              <span className="material-symbols-outlined text-[18px]">
                {copied ? 'check' : 'content_copy'}
              </span>
            </button>
          </div>

          <p className="font-body text-xs text-[#7A6E73] font-medium max-w-[280px]">
            Share this secure code with your partner to link your sanctuaries.
          </p>
        </div>

        {/* Primary Action Button */}
        <div className="w-full">
          <button
            onClick={() => onNavigate('connect')}
            className="w-full glow-button text-white font-display font-bold text-base py-4 rounded-full flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer"
          >
            <span>Connect With Partner</span>
            <span className="material-symbols-outlined text-[20px]">
              arrow_forward
            </span>
          </button>
        </div>
      </main>
    </div>
  );
};
