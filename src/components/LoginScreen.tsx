import React, { useState } from 'react';
import { ScreenType } from '../types';

interface LoginScreenProps {
  onNavigate: (screen: ScreenType) => void;
  onLoginSuccess: (phone: string) => void;
}

export const LoginScreen: React.FC<LoginScreenProps> = ({
  onNavigate,
  onLoginSuccess,
}) => {
  const [phone, setPhone] = useState('9876543210');
  const [password, setPassword] = useState('••••••••');
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onLoginSuccess(phone);
    onNavigate('home');
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6 relative z-10">
      <main className="w-full max-w-md">
        <div className="glass-panel rounded-[28px] p-8 md:p-10 flex flex-col items-center bg-white/5 border border-white/15 shadow-[0_16px_48px_rgba(0,0,0,0.5)] animate-enter">
          {/* Brand Anchor */}
          <div className="mb-8 text-center">
            <h1 className="font-display text-4xl md:text-5xl font-bold bg-gradient-to-r from-[#ffcbd5] to-[#dfbbe4] bg-clip-text text-transparent tracking-tight">
              PairLink
            </h1>
            <p className="font-body text-[16px] text-[#d6c1c5] mt-2">
              Enter your sanctuary.
            </p>
          </div>

          {/* Login Form */}
          <form onSubmit={handleSubmit} className="w-full flex flex-col gap-5">
            {/* Phone Number Input */}
            <div className="relative w-full">
              <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 pointer-events-none text-[20px]">
                call
              </span>
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="Phone Number"
                required
                className="w-full bg-transparent border-0 border-b border-white/20 text-[#dae2fd] placeholder:text-[#d6c1c5]/50 pl-12 pr-4 py-3 font-body text-[16px] glass-input transition-colors rounded-none focus:ring-0"
              />
            </div>

            {/* Password Input */}
            <div className="relative w-full mt-1">
              <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 pointer-events-none text-[20px]">
                lock
              </span>
              <input
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Password"
                required
                className="w-full bg-transparent border-0 border-b border-white/20 text-[#dae2fd] placeholder:text-[#d6c1c5]/50 pl-12 pr-12 py-3 font-body text-[16px] glass-input transition-colors rounded-none focus:ring-0"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 hover:text-[#ffcbd5] transition-colors focus:outline-none p-1"
                aria-label="Toggle password visibility"
              >
                <span className="material-symbols-outlined text-[20px]">
                  {showPassword ? 'visibility_off' : 'visibility'}
                </span>
              </button>
            </div>

            {/* Forgot Password Link */}
            <div className="flex justify-end mt-0.5">
              <button
                type="button"
                onClick={() => alert('Password reset link sent to your registered phone number!')}
                className="font-body text-xs text-[#ffcbd5] hover:text-[#f4a7b9] transition-colors hover:underline"
              >
                Forgot Password?
              </button>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              className="w-full mt-4 glass-panel text-[#ffcbd5] font-display font-medium text-lg py-4 rounded-full glow-button flex items-center justify-center gap-2 border border-white/20 bg-white/10 hover:bg-white/20 active:scale-98"
            >
              <span>Login</span>
              <span className="material-symbols-outlined text-[20px]">
                arrow_forward
              </span>
            </button>
          </form>

          {/* Register Link */}
          <div className="mt-8 text-center font-body text-sm text-[#d6c1c5]">
            Don't have an account?{' '}
            <button
              onClick={() => onNavigate('register')}
              className="text-[#dfbbe4] hover:text-[#fcd7ff] font-semibold transition-colors underline underline-offset-4 decoration-[#dfbbe4]/40 hover:decoration-[#dfbbe4]"
            >
              Register
            </button>
          </div>
        </div>
      </main>
    </div>
  );
};
