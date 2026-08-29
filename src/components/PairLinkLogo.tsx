import React from 'react';

interface PairLinkLogoProps {
  size?: 'sm' | 'md' | 'lg' | 'xl';
  showTagline?: boolean;
  className?: string;
  iconOnly?: boolean;
}

export const PairLinkLogo: React.FC<PairLinkLogoProps> = ({
  size = 'md',
  showTagline = true,
  className = '',
  iconOnly = false,
}) => {
  const iconSizes = {
    sm: 'w-10 h-10',
    md: 'w-16 h-16',
    lg: 'w-24 h-24',
    xl: 'w-32 h-32',
  };

  const textSizes = {
    sm: 'text-lg',
    md: 'text-2xl',
    lg: 'text-4xl',
    xl: 'text-5xl',
  };

  const taglineSizes = {
    sm: 'text-[10px]',
    md: 'text-xs',
    lg: 'text-sm',
    xl: 'text-base',
  };

  return (
    <div className={`flex flex-col items-center justify-center ${className}`}>
      {/* 3D Interlocking Hearts Graphic */}
      <div className={`relative ${iconSizes[size]} drop-shadow-[0_10px_20px_rgba(230,0,57,0.25)] transition-transform duration-300 hover:scale-105`}>
        <svg
          viewBox="0 0 160 140"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          className="w-full h-full filter drop-shadow-md"
        >
          <defs>
            {/* Crimson Red Gradient */}
            <linearGradient id="redHeartGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#FF1E56" />
              <stop offset="50%" stopColor="#E60039" />
              <stop offset="100%" stopColor="#990024" />
            </linearGradient>

            {/* Soft Pink Highlight Gradient */}
            <linearGradient id="pinkHeartGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#FFF0F3" />
              <stop offset="40%" stopColor="#FFB3C1" />
              <stop offset="100%" stopColor="#FF758F" />
            </linearGradient>

            {/* Inner Center Heart Gradient */}
            <linearGradient id="centerHeartGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#FF3366" />
              <stop offset="100%" stopColor="#CC0033" />
            </linearGradient>

            {/* Glossy Overlay Gradient */}
            <linearGradient id="glossGrad" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stopColor="#FFFFFF" stopOpacity="0.8" />
              <stop offset="100%" stopColor="#FFFFFF" stopOpacity="0" />
            </linearGradient>

            <filter id="softGlow" x="-20%" y="-20%" width="140%" height="140%">
              <feGaussianBlur stdDeviation="6" result="blur" />
              <feComposite in="SourceGraphic" in2="blur" operator="over" />
            </filter>
          </defs>

          {/* Background Outer Soft Glow */}
          <path
            d="M50 25 C30 5, 5 30, 35 65 L80 115 L125 65 C155 30, 130 5, 110 25 L80 52 Z"
            fill="#E60039"
            opacity="0.15"
            filter="url(#softGlow)"
          />

          {/* Left Red Heart Loop */}
          <path
            d="M55 20 C32 2, 8 28, 32 62 L80 112 L92 100 L48 54 C34 38, 44 22, 58 32 C68 40, 75 52, 80 58 C85 52, 92 40, 102 32 C116 22, 126 38, 112 54 L100 66 L112 78 L128 62 C152 28, 128 2, 105 20 C92 30, 80 44, 80 44 C80 44, 68 30, 55 20 Z"
            fill="url(#redHeartGrad)"
          />

          {/* Right Pink Interlocking Loop */}
          <path
            d="M95 30 C110 16, 138 32, 122 58 L80 102 L44 64 L56 52 L80 78 L110 46 C120 34, 104 22, 94 32 L80 48 L68 34 Z"
            fill="url(#pinkHeartGrad)"
          />

          {/* Interlocking Front Overlap for 3D depth */}
          <path
            d="M68 34 C74 26, 84 36, 80 48 C76 42, 72 38, 68 34 Z"
            fill="url(#redHeartGrad)"
          />

          {/* Center Inner Solid 3D Heart */}
          <path
            d="M80 50 C74 42, 66 48, 72 57 L80 66 L88 57 C94 48, 86 42, 80 50 Z"
            fill="url(#centerHeartGrad)"
          />

          {/* Gloss Highlight Curves */}
          <path
            d="M32 24 C40 16, 52 18, 56 26 C46 22, 36 24, 30 32 Z"
            fill="url(#glossGrad)"
            opacity="0.7"
          />
          <path
            d="M128 24 C120 16, 108 18, 104 26 C114 22, 124 24, 130 32 Z"
            fill="url(#glossGrad)"
            opacity="0.5"
          />
        </svg>
      </div>

      {!iconOnly && (
        <div className="flex flex-col items-center mt-3">
          {/* PairLink Title */}
          <div className={`font-display font-extrabold tracking-tight ${textSizes[size]} flex items-center`}>
            <span className="text-[#2D2226]">Pair</span>
            <span className="text-[#E60039] relative ml-[1px]">
              Link
              {/* Small heart above the i in Link */}
              <span className="absolute -top-2 left-[11px] text-[10px] text-[#E60039] animate-pulse">
                ♥
              </span>
            </span>
          </div>

          {/* Subtitle Tagline */}
          {showTagline && (
            <div className={`flex items-center gap-1.5 mt-1 font-semibold text-[#7A6E73] ${taglineSizes[size]} tracking-wide uppercase`}>
              <span className="h-[1px] w-4 bg-[#E60039]/40" />
              <span className="text-[#E60039]">♥</span>
              <span>Distance can't break us</span>
              <span className="text-[#E60039]">♥</span>
              <span className="h-[1px] w-4 bg-[#E60039]/40" />
            </div>
          )}
        </div>
      )}
    </div>
  );
};
