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
    <div className="pt-20 pb-32 min-h-screen flex flex-col items-center justify-start px-4 sm:px-6 relative z-10 w-full max-w-lg mx-auto">
      <h1 className="font-display font-extrabold text-2xl text-[#2D2226] text-center mb-6 tracking-tight mt-2">
        How are you feeling? ❤️
      </h1>

      {/* Floating Light Chips Container */}
      <div className="flex flex-wrap justify-center gap-3 w-full mb-8">
        {MOOD_OPTIONS.map((item) => {
          const isSelected = selectedMood.toLowerCase() === item.label.toLowerCase() && !customMood;
          return (
            <button
              key={item.id}
              onClick={() => {
                setSelectedMood(item.label);
                setCustomMood('');
              }}
              className={`mood-chip ${item.floatClass} bg-white/90 backdrop-blur-2xl border ${
                isSelected ? 'border-[#E60039] bg-rose-50/80 shadow-md text-[#E60039]' : 'border-white/90 shadow-xs text-[#2D2226] hover:bg-rose-50/40'
              } rounded-full px-5 py-3 flex items-center gap-2 z-10 group cursor-pointer transition-all active:scale-95`}
            >
              <span
                className={`material-symbols-outlined text-[20px] transition-colors ${
                  isSelected ? 'text-[#E60039]' : 'text-[#7A6E73] group-hover:text-[#E60039]'
                }`}
                style={{ fontVariationSettings: isSelected ? "'FILL' 1" : "'FILL' 0" }}
              >
                {item.icon}
              </span>
              <span
                className={`font-body text-xs ${
                  isSelected ? 'text-[#E60039] font-bold' : 'text-[#2D2226] font-semibold'
                }`}
              >
                {item.label}
              </span>
            </button>
          );
        })}
      </div>

      {/* Custom Mood Input */}
      <div className="w-full mt-auto mb-5">
        <div className="relative w-full">
          <input
            type="text"
            value={customMood}
            onChange={(e) => setCustomMood(e.target.value)}
            placeholder="Create Custom Mood..."
            className="w-full bg-white/90 border border-rose-200/80 rounded-2xl py-3.5 px-5 text-[#2D2226] placeholder:text-[#7A6E73]/60 focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all font-body text-sm shadow-xs"
          />
          <span className="material-symbols-outlined absolute right-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
            edit
          </span>
        </div>
      </div>

      {/* Save Button */}
      <button
        onClick={handleSave}
        className="w-full glow-button text-white font-display font-bold text-base py-4 rounded-2xl flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer"
      >
        Save Mood
      </button>
    </div>
  );
};
