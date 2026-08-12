import React, { useState } from 'react';
import { ScreenType } from '../types';

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
      <main className="w-full max-w-md mx-auto my-auto flex flex-col gap-8 py-10">
        <header className="text-center mb-2">
          <h1 className="font-display font-bold text-3xl md:text-4xl text-[#ffcbd5] mb-2 leading-tight">
            Let's Personalize Your Journey ❤️
          </h1>
          <p className="font-body text-[#d6c1c5] text-base">
            Set your special date to unlock milestones.
          </p>
        </header>

        <form onSubmit={handleSubmit} className="flex flex-col gap-6">
          <section className="glass-panel rounded-[24px] p-6 flex flex-col gap-4 bg-white/10 border border-white/20">
            <div className="flex flex-col gap-2">
              <label
                htmlFor="relationship-date"
                className="font-body text-xs text-[#dae2fd] uppercase tracking-wider pl-1 font-semibold"
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
                  className="w-full border border-white/15 rounded-xl px-4 py-3.5 font-body text-base text-[#dae2fd] focus:outline-none focus:border-[#ffcbd5]/60 focus:ring-1 focus:ring-[#ffcbd5]/60 transition-all cursor-pointer appearance-none bg-white/15"
                />
                <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-[#ffcbd5]/80">
                  <span className="material-symbols-outlined text-[20px]">
                    calendar_month
                  </span>
                </div>
              </div>
            </div>

            <p className="font-body text-xs text-[#d6c1c5] text-center px-2 mt-2 leading-relaxed">
              This date is used to celebrate your anniversaries and track shared moments. You can change this later in settings.
            </p>
          </section>

          {/* Large Glass Save Button */}
          <button
            type="submit"
            className="glass-panel rounded-full py-4 px-8 w-full font-display font-bold text-lg text-[#ffcbd5] tracking-wide flex justify-center items-center gap-2 hover:bg-white/15 transition-all active:scale-95 glow-button bg-white/10 border border-white/20"
          >
            <span>Start Our Journey</span>
            <span className="material-symbols-outlined transition-transform group-hover:translate-x-1">
              arrow_forward
            </span>
          </button>
        </form>
      </main>
    </div>
  );
};
