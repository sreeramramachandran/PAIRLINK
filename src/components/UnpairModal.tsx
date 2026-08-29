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
    <div className="fixed inset-0 z-50 flex items-center justify-center p-6 bg-[#2D2226]/40 backdrop-blur-md">
      <div className="relative w-full max-w-sm rounded-3xl bg-white/95 backdrop-blur-2xl border border-white p-6 shadow-[0_20px_50px_rgba(230,0,57,0.15)] modal-animate">
        <div className="flex flex-col items-center text-center space-y-4 py-2">
          {/* Icon container */}
          <div className="w-14 h-14 rounded-2xl bg-rose-50 border border-rose-200 flex items-center justify-center text-[#E60039] shadow-sm mb-1">
            <span
              className="material-symbols-outlined text-3xl"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              heart_broken
            </span>
          </div>

          {/* Text Content */}
          <div className="space-y-1">
            <h2 className="font-display font-extrabold text-2xl text-[#2D2226]">
              Unlink Partner?
            </h2>
            <p className="font-body text-xs text-[#7A6E73] font-medium leading-relaxed px-2">
              This will remove your shared connection and sanctuary data with your partner.
            </p>
          </div>

          {/* Actions */}
          <div className="w-full pt-3 space-y-2.5">
            <button
              onClick={() => {
                onUnpairConfirm();
                onNavigate('connect');
              }}
              className="w-full py-3.5 rounded-full bg-[#E60039] text-white font-display font-bold text-sm shadow-md hover:bg-[#C4002F] active:scale-95 transition-all cursor-pointer"
            >
              Unlink
            </button>

            <button
              onClick={onCancel}
              className="w-full py-3.5 rounded-full bg-rose-50 text-[#7A6E73] hover:text-[#2D2226] font-display font-bold text-sm transition-all border border-rose-200/80 active:scale-95 cursor-pointer"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
