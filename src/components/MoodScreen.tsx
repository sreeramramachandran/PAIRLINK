import React, { useState } from 'react';
import { ScreenType } from '../types';

interface MoodScreenProps {
  currentMood: string;
  onSaveMood: (mood: string) => void;
  onNavigate: (screen: ScreenType) => void;
}

const MOOD_OPTIONS = [
  { id: 'happy', label: 'Happy', icon: 'sentiment_satisfied', floatClass: 'float-1' },
  { id: 'missing_you', label: 'Missing You', icon: 'favorite_border', floatClass: 'float-2' },
  { id: 'sleeping', label: 'Sleeping', icon: 'bedtime', floatClass: 'float-3' },
  { id: 'busy', label: 'Busy', icon: 'work', floatClass: 'float-1' },
  { id: 'driving', label: 'Driving', icon: 'directions_car', floatClass: 'float-4' },
  { id: 'sad', label: 'Sad', icon: 'sentiment_dissatisfied', floatClass: 'float-2' },
  { id: 'thinking_of_you', label: 'Thinking of You', icon: 'psychology', floatClass: 'float-3' },
  { id: 'need_a_hug', label: 'Need a Hug', icon: 'volunteer_activism', floatClass: 'float-1' },
  { id: 'excited', label: 'Excited', icon: 'celebration', floatClass: 'float-4' },
];

export const MoodScreen: React.FC<MoodScreenProps> = ({
  currentMood,
  onSaveMood,
  onNavigate,
}) => {
  const [selectedMood, setSelectedMood] = useState(currentMood);
  const [customMood, setCustomMood] = useState('');

  const handleSave = () => {
    const finalMood = customMood.trim() || selectedMood;
    onSaveMood(finalMood);
    onNavigate('home');
  };

  return (
    <div className="pt-24 pb-32 min-h-screen flex flex-col items-center justify-start px-6 relative z-10 w-full max-w-lg mx-auto">
      <h1 className="font-display font-light text-2xl md:text-3xl text-[#dae2fd] text-center mb-8 tracking-wide drop-shadow-md mt-2">
        How are you feeling?
      </h1>

      {/* Floating Glass Chips Container */}
      <div className="flex flex-wrap justify-center gap-3.5 w-full mb-8">
        {MOOD_OPTIONS.map((item) => {
          const isSelected = selectedMood.toLowerCase() === item.label.toLowerCase() && !customMood;
          return (
            <button
              key={item.id}
              onClick={() => {
                setSelectedMood(item.label);
                setCustomMood('');
              }}
              className={`mood-chip ${item.floatClass} backdrop-blur-xl border border-white/20 rounded-full px-5 py-3 flex items-center gap-2 hover:bg-white/10 z-10 group bg-white/15 cursor-pointer ${
                isSelected ? 'selected' : ''
              }`}
            >
              <span
                className={`material-symbols-outlined text-[20px] transition-colors ${
                  isSelected ? 'text-[#ffcbd5]' : 'text-[#d6c1c5] group-hover:text-[#ffcbd5]'
                }`}
                data-weight={isSelected ? 'fill' : 'none'}
                style={{ fontVariationSettings: isSelected ? "'FILL' 1" : "'FILL' 0" }}
              >
                {item.icon}
              </span>
              <span
                className={`font-body text-sm transition-colors ${
                  isSelected ? 'text-[#ffcbd5] font-semibold' : 'text-[#d6c1c5] group-hover:text-[#dae2fd]'
                }`}
              >
                {item.label}
              </span>
            </button>
          );
        })}
      </div>

      {/* Custom Mood Input */}
      <div className="w-full mt-auto mb-6">
        <div className="relative w-full">
          <input
            type="text"
            value={customMood}
            onChange={(e) => setCustomMood(e.target.value)}
            placeholder="Create Custom Mood..."
            className="w-full border border-white/20 rounded-2xl py-4 px-6 text-[#dae2fd] placeholder:text-[#d6c1c5]/50 focus:outline-none focus:border-[#ffcbd5]/50 focus:bg-white/15 transition-all backdrop-blur-xl font-body bg-white/10"
          />
          <span className="material-symbols-outlined absolute right-5 top-1/2 -translate-y-1/2 text-[#d6c1c5]/50 pointer-events-none text-[20px]">
            edit
          </span>
        </div>
      </div>

      {/* Save Button */}
      <button
        onClick={handleSave}
        className="w-full bg-[#ffcbd5] text-[#521f2e] font-display font-medium text-lg py-4 rounded-2xl shadow-[0_0_20px_rgba(244,167,185,0.4)] hover:shadow-[0_0_25px_rgba(244,167,185,0.6)] hover:bg-[#ffd9e0] transition-all active:scale-[0.98] backdrop-blur-xl border border-white/20 cursor-pointer"
      >
        Save Mood
      </button>
    </div>
  );
};
