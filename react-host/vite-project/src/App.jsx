import React from 'react'
import DownloadButton from './components/DownloadButton'
import './App.css'
import SocialLinks from './components/SocialLinks'

function App() {
  return (
    <div className="app">
      <div className="hero-section">
        <h1>QNPSO-ML Driven Cloud Resource Management</h1>
        <p className="hero-text">
          Cloud simulator
        </p>
        <DownloadButton />
        <SocialLinks />
      </div>

      <div className="feature-grid">
        <div className="feature-card">
          <h3>ML Prediction</h3>
          <p>server load prediction using Random Forest model</p>
        </div>
        <div className="feature-card">
          <h3>QNPSO Optimization</h3>
          <p>Quantum-behaved neural particle swarm optimization for VM allocation</p>
        </div>
        <div className="feature-card">
          <h3>Real-time Monitoring</h3>
          <p>Live metrics and performance monitoring dashboard</p>
        </div>
      </div>

      <div className="tech-stack">
        <h2>Built With</h2>
        <div className="tech-icons">
          <span>Java</span>
          <span>OOPs</span>
          <span>SQL</span>
        </div>
      </div>
    </div>
  )
}

export default App
