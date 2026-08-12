import React from 'react';
import { ScreenType, PartnerProfile } from '../types';

interface PairRequestModalProps {
  partner: PartnerProfile;
  onAccept: () => void;
  onReject: () => void;
  onNavigate: (screen: ScreenType) => void;
}

export const PairRequestModal: React.FC<PairRequestModalProps> = ({
  partner,
  onAccept,
  onReject,
  onNavigate,
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-6 bg-[#0b1326]/60 backdrop-blur-md">
      {/* Blurred background context */}
      <div className="absolute inset-0 z-0 opacity-20 pointer-events-none flex flex-col items-center justify-center gap-6 scale-105 blur-md">
        <div className="w-48 h-48 rounded-full border-4 border-[#ffcbd5]/20 flex items-center justify-center">
          <div className="w-32 h-32 rounded-full border-4 border-[#ffcbd5]/40 animate-pulse" />
        </div>
        <div className="font-display text-xl text-[#d6c1c5]">
          Searching for partner...
        </div>
      </div>

      {/* Floating Glass Popup Card */}
      <div className="relative z-20 w-full max-w-md glass-card rounded-[32px] p-8 shadow-[0_0_40px_rgba(244,167,185,0.25)] flex flex-col items-center text-center gap-6 modal-animate bg-white/10 border border-white/20">
        {/* Profile Picture with Glow */}
        <div className="relative">
          <div className="absolute inset-0 bg-[#ffcbd5] rounded-full blur-xl opacity-30 animate-pulse" />
          <img
            src={partner.avatarUrl}
            alt={partner.name}
            className="w-32 h-32 rounded-full object-cover border-2 border-white/30 relative z-10 shadow-xl"
          />
          {/* Mood Indicator */}
          <div className="absolute bottom-1 right-1 w-6 h-6 rounded-full bg-[#dfbbe4] border-2 border-[#0b1326] z-20 shadow-[0_0_12px_rgba(223,187,228,0.8)]" />
        </div>

        {/* Text Content */}
        <div className="flex flex-col gap-2">
          <h2 className="font-display font-bold text-3xl text-[#dae2fd]">
            {partner.name}
          </h2>
          <p className="font-body text-base text-[#d6c1c5] max-w-[260px] mx-auto">
            {partner.name} wants to connect with you.
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col w-full gap-3 mt-2">
          <button
            onClick={() => {
              onAccept();
              onNavigate('connected_forever');
            }}
            className="w-full py-4 rounded-full glass-card bg-[#ffcbd5]/25 text-[#dae2fd] font-display font-semibold text-lg shadow-[0_0_20px_rgba(244,167,185,0.4)] hover:bg-[#ffcbd5]/40 active:scale-95 transition-all border border-white/20"
          >
            Accept
          </button>
          <button
            onClick={() => {
              onReject();
              onNavigate('connect');
            }}
            className="w-full py-4 rounded-full glass-card text-[#dae2fd] font-display font-semibold text-lg hover:bg-white/10 active:scale-95 transition-all shadow-xl border border-white/10"
          >
            Reject
          </button>
        </div>
      </div>
    </div>
  );
};
