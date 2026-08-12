import React, { useState } from 'react';
import { ScreenType } from '../types';

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
      <main className="w-full max-w-md mx-auto my-auto flex flex-col items-center justify-center py-10">
        {/* Header Text */}
        <div className="w-full text-center mb-6">
          <h1 className="font-display text-4xl md:text-5xl font-bold bg-gradient-to-r from-[#ffcbd5] to-[#dfbbe4] bg-clip-text text-transparent mb-2">
            Connect
          </h1>
          <p className="font-display text-xl text-[#d6c1c5]">
            With Your Partner
          </p>
        </div>

        {/* Romantic Illustration Area */}
        <div className="w-full aspect-square max-w-[260px] mx-auto mb-8 relative rounded-full overflow-hidden shadow-[0_0_40px_rgba(244,167,185,0.2)] flex items-center justify-center p-4">
          <div className="absolute inset-0 bg-gradient-to-tr from-[#ffcbd5]/20 to-[#dfbbe4]/20 rounded-full animate-pulse blur-xl" />
          <img
            src="https://lh3.googleusercontent.com/aida/AP1WRLtnEh5Gey16uCEtGEQFj00k24zPciWv2WzuPZBGzV2DoisOstpWwdi6GEykFCvrCsrS69tjqn5eQumJZu3gg5ifYYVAIVQcduPTHIsmzIvsArs5VjOw6LCC5_yUs0PBjOARp5qhBqhRrRproE3KL9PFML1fDd3AJhgyT1RMBv56hkBtPzEVg0wLbt6vUMiznDmCiJCoRr6liseo3B5iZf5wURfOmz1me2QbfPKmNKudpG2KuWvC1qKBUdLi"
            alt="Romantic glass hearts illustration"
            className="w-full h-full object-cover rounded-full mix-blend-screen opacity-90 relative z-10"
          />
        </div>

        {/* Pairing Form Card */}
        <form
          onSubmit={handleSubmit}
          className="glass-card rounded-2xl w-full p-6 flex flex-col gap-6 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] bg-white/10"
        >
          {/* Input Field */}
          <div className="relative w-full">
            <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-[#d6c1c5]/60 text-[20px]">
              vpn_key
            </span>
            <input
              type="text"
              value={targetId}
              onChange={(e) => setTargetId(e.target.value)}
              placeholder="Enter Partner ID"
              required
              className="glass-input w-full py-4 pl-10 pr-4 font-body text-lg text-center bg-white/5 rounded-xl text-[#dae2fd]"
            />
          </div>

          {/* Action Button */}
          <button
            type="submit"
            className="w-full bg-[#f4a7b9]/40 text-[#ffcbd5] font-body text-xs font-bold py-4 rounded-full glow-button uppercase tracking-widest flex items-center justify-center gap-2 border border-white/20 hover:bg-[#f4a7b9]/60 transition-all active:scale-98"
          >
            <span>Connect</span>
            <span
              className="material-symbols-outlined text-[18px]"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              favorite
            </span>
          </button>

          <div className="text-center">
            <button
              type="button"
              onClick={() => alert('Ask your partner to check their PairLink app settings or onboarding screen to view their Partner ID!')}
              className="text-[#d6c1c5]/80 hover:text-[#ffcbd5] transition-colors font-body text-xs"
            >
              Need help finding your ID?
            </button>
          </div>
        </form>
      </main>
    </div>
  );
};
