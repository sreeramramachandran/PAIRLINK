import React from 'react';
import { ScreenType, UpdateNotification } from '../types';

interface UpdatesScreenProps {
  notifications: UpdateNotification[];
  partnerName: string;
  onNavigate: (screen: ScreenType) => void;
}

export const UpdatesScreen: React.FC<UpdatesScreenProps> = ({
  notifications,
  partnerName,
  onNavigate,
}) => {
  return (
    <div className="pt-24 pb-32 px-6 max-w-lg mx-auto relative z-10 flex flex-col gap-4">
      <div className="mb-2">
        <h2 className="font-display font-bold text-2xl md:text-3xl text-[#dae2fd] mb-1">
          Updates
        </h2>
        <p className="font-body text-sm text-[#d6c1c5]">
          Stay close to your partner.
        </p>
      </div>

      {/* Notification Timeline */}
      <div className="relative pl-6 before:content-[''] before:absolute before:left-3 before:top-4 before:bottom-4 before:w-[2px] before:bg-gradient-to-b before:from-[#f4a7b9]/50 before:to-transparent space-y-6">
        {notifications.map((item) => {
          let dotBg = 'bg-[#f4a7b9] shadow-[0_0_12px_rgba(244,167,185,0.8)]';
          let iconBg = 'bg-[#f4a7b9]/15 text-[#ffcbd5]';

          if (item.colorType === 'secondary') {
            dotBg = 'bg-[#593d5f] shadow-[0_0_12px_rgba(89,61,95,0.8)]';
            iconBg = 'bg-[#593d5f]/30 text-[#dfbbe4]';
          } else if (item.colorType === 'tertiary') {
            dotBg = 'bg-[#ffa3ab] shadow-[0_0_12px_rgba(255,163,171,0.8)]';
            iconBg = 'bg-[#ffa3ab]/20 text-[#ffcbcf]';
          }

          return (
            <div key={item.id} className="relative group">
              {/* Dot */}
              <div
                className={`absolute -left-[27px] top-6 w-3 h-3 rounded-full ${dotBg} z-10 ring-4 ring-[#0b1326]`}
              />

              {/* Glass Card */}
              <div
                onClick={() => {
                  if (item.title.toLowerCase().includes('birthday')) {
                    onNavigate('birthday');
                  } else if (item.title.toLowerCase().includes('mood')) {
                    onNavigate('mood');
                  }
                }}
                className="glass-card rounded-xl p-5 transform transition-all duration-300 hover:scale-[1.02] cursor-pointer bg-white/10 border border-white/15"
              >
                <div className="flex items-start gap-4">
                  <div
                    className={`w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0 ${iconBg} border border-white/10`}
                  >
                    <span
                      className="material-symbols-outlined text-[24px]"
                      data-weight="fill"
                      style={{ fontVariationSettings: "'FILL' 1" }}
                    >
                      {item.icon}
                    </span>
                  </div>

                  <div className="flex-1">
                    <div className="flex justify-between items-baseline mb-1">
                      <h3 className="font-display font-medium text-base text-[#dae2fd]">
                        {item.title}
                      </h3>
                      <span className="font-body text-xs text-[#d6c1c5]/80 ml-2">
                        {item.time}
                      </span>
                    </div>
                    <p className="font-body text-sm text-[#d6c1c5] leading-relaxed">
                      {item.description}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
