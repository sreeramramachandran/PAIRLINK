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
    <div className="fixed inset-0 z-50 flex items-center justify-center p-6 bg-[#2D2226]/40 backdrop-blur-md">
      {/* Floating Light Popup Card */}
      <div className="relative z-20 w-full max-w-md bg-white/95 backdrop-blur-2xl rounded-3xl p-8 shadow-[0_20px_50px_rgba(230,0,57,0.15)] flex flex-col items-center text-center gap-6 modal-animate border border-white">
        {/* Profile Picture with Glow */}
        <div className="relative">
          <div className="w-28 h-28 rounded-full overflow-hidden border-4 border-rose-200 shadow-xl relative z-10 p-0.5 bg-white">
            <img
              src={partner.avatarUrl}
              alt={partner.name}
              className="w-full h-full rounded-full object-cover"
            />
          </div>
          {/* Heart Badge */}
          <div className="absolute -bottom-1 -right-1 w-8 h-8 rounded-full bg-[#E60039] text-white flex items-center justify-center z-20 shadow-md border-2 border-white">
            <span
              className="material-symbols-outlined text-[16px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
          </div>
        </div>

        {/* Text Content */}
        <div className="flex flex-col gap-1.5">
          <h2 className="font-display font-extrabold text-2xl text-[#2D2226]">
            {partner.name}
          </h2>
          <p className="font-body text-sm text-[#7A6E73] max-w-[260px] mx-auto font-medium">
            {partner.name} wants to connect with you on PairLink!
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col w-full gap-3 mt-2">
          <button
            onClick={() => {
              onAccept();
              onNavigate('connected_forever');
            }}
            className="w-full py-3.5 rounded-full glow-button text-white font-display font-bold text-base shadow-lg transition-all active:scale-95 cursor-pointer"
          >
            Accept Request ❤️
          </button>
          <button
            onClick={() => {
              onReject();
              onNavigate('connect');
            }}
            className="w-full py-3.5 rounded-full bg-rose-50 text-[#7A6E73] hover:text-[#2D2226] hover:bg-rose-100 font-display font-semibold text-sm transition-all active:scale-95 border border-rose-200/80 cursor-pointer"
          >
            Decline
          </button>
        </div>
      </div>
    </div>
  );
};
