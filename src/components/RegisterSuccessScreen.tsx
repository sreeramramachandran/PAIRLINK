import React, { useState } from 'react';
import { ScreenType } from '../types';

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
      {/* Ambient glowing radiance */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[90vw] h-[90vw] max-w-[600px] max-h-[600px] bg-[#ffcbd5]/20 rounded-full animate-radiance -z-10 pointer-events-none" />

      <main className="relative z-10 flex-1 flex flex-col items-center justify-center w-full max-w-lg mx-auto my-auto py-12">
        {/* Celebration Icon */}
        <div className="mb-6 animate-enter">
          <div className="w-24 h-24 rounded-full bg-white/20 border border-white/20 backdrop-blur-2xl flex items-center justify-center shadow-[0_8px_32px_0_rgba(0,0,0,0.4)]">
            <span
              className="material-symbols-outlined text-[#ffcbd5] text-5xl"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
          </div>
        </div>

        {/* Greeting */}
        <h1 className="font-display font-bold text-3xl md:text-5xl text-center mb-6 bg-gradient-to-br from-[#ffcbd5] to-[#dfbbe4] bg-clip-text text-transparent drop-shadow-[0_2px_10px_rgba(0,0,0,0.5)]">
          Welcome, {username || 'Alex'}!
        </h1>

        {/* Partner ID Glass Card */}
        <div className="w-full backdrop-blur-2xl border border-white/20 rounded-[24px] p-8 flex flex-col items-center gap-6 shadow-[0_8px_32px_0_rgba(0,0,0,0.4)] mb-8 relative overflow-hidden bg-white/10">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-white/40 to-transparent" />
          <span className="font-body text-xs text-[#d6c1c5] uppercase tracking-wider font-semibold">
            Your Partner ID
          </span>

          <div className="flex items-center justify-center gap-4 w-full">
            <span className="font-display text-2xl md:text-3xl text-[#dae2fd] tracking-wider font-bold">
              {partnerId}
            </span>

            {/* Copy Button */}
            <button
              onClick={copyId}
              aria-label="Copy Partner ID"
              className={`w-12 h-12 flex items-center justify-center rounded-full border transition-all duration-300 backdrop-blur-2xl active:scale-95 ${
                copied
                  ? 'bg-emerald-500/20 border-emerald-400 text-emerald-300'
                  : 'bg-white/20 border-white/20 text-[#ffcbd5] hover:bg-white/30'
              }`}
            >
              <span className="material-symbols-outlined text-[20px]">
                {copied ? 'check' : 'content_copy'}
              </span>
            </button>
          </div>

          <p className="font-body text-sm text-[#d6c1c5]/80 text-center max-w-[280px]">
            Share this secure code with your partner to link your sanctuaries.
          </p>
        </div>

        {/* Primary Action Button */}
        <div className="w-full">
          <button
            onClick={() => onNavigate('connect')}
            className="w-full bg-[#ffcbd5] text-[#521f2e] font-display font-medium text-lg py-4 rounded-full shadow-[0_0_24px_rgba(244,167,185,0.4)] hover:shadow-[0_0_32px_rgba(244,167,185,0.7)] hover:bg-[#ffd9e0] transition-all duration-300 active:scale-[0.98] relative overflow-hidden group border border-white/20"
          >
            <span className="relative z-10 flex items-center justify-center gap-2 font-bold">
              Connect With Partner
              <span className="material-symbols-outlined text-[20px]">
                arrow_forward
              </span>
            </span>
          </button>
        </div>
      </main>
    </div>
  );
};
