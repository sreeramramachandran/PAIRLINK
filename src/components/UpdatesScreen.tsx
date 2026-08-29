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
    <div className="pt-20 pb-32 px-4 sm:px-6 max-w-lg mx-auto relative z-10 flex flex-col gap-4">
      <div className="mb-2 text-center sm:text-left">
        <h2 className="font-display font-extrabold text-2xl text-[#2D2226] mb-1">
          Updates & Notifications
        </h2>
        <p className="font-body text-xs text-[#7A6E73] font-medium">
          Stay close to {partnerName} and never miss a heartbeat.
        </p>
      </div>

      {/* Notification Timeline */}
      <div className="relative pl-6 before:content-[''] before:absolute before:left-3 before:top-4 before:bottom-4 before:w-[2px] before:bg-rose-200 space-y-4">
        {notifications.map((item) => {
          let dotBg = 'bg-[#E60039] shadow-sm';
          let iconBg = 'bg-rose-50 text-[#E60039] border-rose-200/80';

          if (item.colorType === 'secondary') {
            dotBg = 'bg-rose-400 shadow-sm';
            iconBg = 'bg-pink-50 text-rose-500 border-pink-200/80';
          } else if (item.colorType === 'tertiary') {
            dotBg = 'bg-[#FF3366] shadow-sm';
            iconBg = 'bg-rose-100 text-[#E60039] border-rose-200';
          }

          return (
            <div key={item.id} className="relative group">
              {/* Dot */}
              <div
                className={`absolute -left-[27px] top-5 w-3 h-3 rounded-full ${dotBg} z-10 ring-4 ring-[#FAF5F5]`}
              />

              {/* Light Neumorphic Card */}
              <div
                onClick={() => {
                  if (item.title.toLowerCase().includes('birthday')) {
                    onNavigate('birthday');
                  } else if (item.title.toLowerCase().includes('mood')) {
                    onNavigate('mood');
                  }
                }}
                className="bg-white/90 backdrop-blur-2xl rounded-2xl p-4 transform transition-all duration-300 hover:translate-y-[-2px] cursor-pointer border border-white/90 shadow-[0_8px_24px_rgba(230,0,57,0.06)]"
              >
                <div className="flex items-start gap-3.5">
                  <div
                    className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${iconBg} border shadow-xs`}
                  >
                    <span
                      className="material-symbols-outlined text-[20px]"
                      style={{ fontVariationSettings: "'FILL' 1" }}
                    >
                      {item.icon}
                    </span>
                  </div>

                  <div className="flex-1">
                    <div className="flex justify-between items-baseline mb-0.5">
                      <h3 className="font-display font-bold text-sm text-[#2D2226]">
                        {item.title}
                      </h3>
                      <span className="font-body text-[10px] font-semibold text-[#7A6E73] ml-2">
                        {item.time}
                      </span>
                    </div>
                    <p className="font-body text-xs text-[#7A6E73] leading-relaxed font-medium">
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
