import React from 'react';
import { ScreenType } from '../types';

interface UnpairModalProps {
  onUnpairConfirm: () => void;
  onCancel: () => void;
  onNavigate: (screen: ScreenType) => void;
}

export const UnpairModal: React.FC<UnpairModalProps> = ({
  onUnpairConfirm,
  onCancel,
  onNavigate,
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-6 bg-[#0b1326]/60 backdrop-blur-[32px]">
      <div className="relative w-full max-w-sm rounded-[32px] bg-white/10 border border-white/20 p-6 shadow-[0_32px_64px_rgba(0,0,0,0.5)] modal-animate overflow-hidden">
        {/* Subtle top light accent */}
        <div className="absolute top-0 left-1/4 right-1/4 h-[1px] bg-gradient-to-r from-transparent via-white/40 to-transparent" />

        <div className="flex flex-col items-center text-center space-y-4 z-10 relative py-2">
          {/* Icon container */}
          <div className="w-16 h-16 rounded-full bg-rose-500/15 border border-rose-400/30 flex items-center justify-center shadow-[0_0_24px_rgba(244,114,182,0.3)] mb-1">
            <span
              className="material-symbols-outlined text-rose-400 text-3xl"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              heart_broken
            </span>
          </div>

          {/* Text Content */}
          <div className="space-y-1.5">
            <h2 className="font-display font-bold text-2xl text-[#dae2fd]">
              Unpair?
            </h2>
            <p className="font-body text-sm text-[#d6c1c5]/80 px-2 leading-relaxed">
              This will permanently remove your connection with your partner.
            </p>
          </div>

          {/* Actions */}
          <div className="w-full pt-4 space-y-3">
            <button
              onClick={() => {
                onUnpairConfirm();
                onNavigate('connect');
              }}
              className="w-full py-3.5 rounded-full bg-rose-500/30 border border-rose-400/40 text-rose-100 font-display font-bold text-base shadow-[0_0_20px_rgba(244,114,182,0.4)] hover:bg-rose-500/40 active:scale-95 transition-all duration-300 cursor-pointer"
            >
              Unpair
            </button>

            <button
              onClick={onCancel}
              className="w-full py-3.5 rounded-full bg-white/10 border border-white/20 text-[#dae2fd] font-display font-semibold text-base hover:bg-white/15 active:scale-95 transition-all duration-300 cursor-pointer"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
