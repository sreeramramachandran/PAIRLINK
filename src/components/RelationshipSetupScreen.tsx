import React, { useState } from 'react';
import { ScreenType } from '../types';
import { PairLinkLogo } from './PairLinkLogo';

interface RelationshipSetupScreenProps {
  onNavigate: (screen: ScreenType) => void;
  onSaveStartDate: (date: string) => void;
}

export const RelationshipSetupScreen: React.FC<RelationshipSetupScreenProps> = ({
  onNavigate,
  onSaveStartDate,
}) => {
  const [startDate, setStartDate] = useState('2022-06-14');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSaveStartDate(startDate);
    onNavigate('home');
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 relative z-10">
      <main className="w-full max-w-md mx-auto my-auto flex flex-col gap-6 py-8">
        <header className="text-center mb-2">
          <PairLinkLogo size="md" showTagline={true} />
          <h1 className="font-display font-extrabold text-2xl text-[#2D2226] mt-4 mb-1">
            Let's Personalize Your Journey ❤️
          </h1>
          <p className="font-body text-[#7A6E73] text-sm font-medium">
            Set your special date to unlock shared milestones.
          </p>
        </header>

        <form onSubmit={handleSubmit} className="flex flex-col gap-5">
          <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-7 flex flex-col gap-4 border border-white/90 shadow-[0_16px_40px_rgba(230,0,57,0.1),0_4px_16px_rgba(0,0,0,0.04)] modal-animate">
            <div className="flex flex-col gap-2">
              <label
                htmlFor="relationship-date"
                className="font-body text-xs text-[#7A6E73] uppercase tracking-wider font-bold"
              >
                Relationship Start Date
              </label>
              <div className="relative w-full">
                <input
                  id="relationship-date"
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  required
                  className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] py-3.5 pl-4 pr-12 font-display font-bold text-base rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all cursor-pointer"
                />
                <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-[#E60039]">
                  <span className="material-symbols-outlined text-[22px]">
                    calendar_month
                  </span>
                </div>
              </div>
            </div>

            <p className="font-body text-xs text-[#7A6E73] text-center px-1 leading-relaxed font-medium">
              This date is used to celebrate your anniversaries and track shared moments. You can change this anytime in settings.
            </p>
          </section>

          {/* Crimson Save Button */}
          <button
            type="submit"
            className="glow-button text-white font-display font-bold text-base py-4 rounded-full flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer"
          >
            <span>Start Our Journey</span>
            <span className="material-symbols-outlined text-[20px]">
              arrow_forward
            </span>
          </button>
        </form>
      </main>
    </div>
  );
};
