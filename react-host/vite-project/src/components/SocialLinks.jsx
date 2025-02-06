import React from 'react';
import { FaGithub, FaLinkedin, FaEnvelope } from 'react-icons/fa';
import { SiLeetcode } from 'react-icons/si';
import './sociallinks.css'

const SocialLinks = () => {
  const links = [
    {
      icon: <FaGithub />,
      url: 'https://github.com/sanjay18257',
      label: 'GitHub'
    },
    {
      icon: <FaLinkedin />,
      url: 'https://www.linkedin.com/in/sanjay-s-1a9719239',
      label: 'LinkedIn'
    },
    {
      icon: <SiLeetcode />,
      url: 'https://leetcode.com/u/codeatsanjay/',
      label: 'LeetCode'
    },
    {
      icon: <FaEnvelope />,
      url: 'mailto:cseskct216sanjay.s@gmail.com',
      label: 'Email'
    }
  ];

  return (
    <div className="social-links">
      {links.map((link, index) => (
        <a 
          key={index}
          href={link.url}
          target="_blank"
          rel="noopener noreferrer"
          className="social-icon"
          aria-label={link.label}
        >
          {link.icon}
        </a>
      ))}
    </div>
  );
};

export default SocialLinks;
