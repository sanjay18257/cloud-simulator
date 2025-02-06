import React, { useState } from 'react';
import './DownloadButton.css';
import JSZip from 'jszip';

const DownloadButton = () => {
  const [downloading, setDownloading] = useState(false);

  const handleDownload = async () => {
    setDownloading(true);
    const zip = new JSZip();
    
    try {
      // Add main JAR
      const mainJarResponse = await fetch('/Power_Consumption_PSO.jar');
      const mainJarBlob = await mainJarResponse.blob();
      zip.file('Power_Consumption_PSO.jar', mainJarBlob);
      
      // List of all dependency JARs
      const libFiles = [
        'cloudsim-3.0.2.jar',
        'commons-csv-1.10.0.jar',
        'itext-pdfa-5.5.13.3.jar',
        'itext-xtra-5.5.13.3.jar',
        'itextpdf-5.5.13.2.jar',
        'itextpdf-5.5.13.3 (3).jar',
        'itextpdf-5.5.13.3.jar',
        'jcommon-1.0.16.jar',
        'jfreechart.jar',
        'mysql-connector-j-9.1.0.jar',
        'mysql-connector-java-8.0.27-sources.jar'
      ];
      
      // Add all dependency JARs
      for (const jarFile of libFiles) {
        const response = await fetch(`/lib/${jarFile}`);
        const blob = await response.blob();
        zip.file(`lib/${jarFile}`, blob);
      }
      
      // Generate and download zip
      const content = await zip.generateAsync({ type: 'blob' });
      const url = window.URL.createObjectURL(content);
      
      const link = document.createElement('a');
      link.href = url;
      link.download = 'Power_Consumption_PSO_App.zip';
      document.body.appendChild(link);
      link.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(link);
      
    } catch (error) {
      console.error('Download failed:', error);
    } finally {
      setDownloading(false);
    }
  };

  return (
    <div className="download-wrapper">
      <button 
        className="download-button" 
        onClick={handleDownload}
        disabled={downloading}
      >
        {downloading ? 'Preparing Download...' : 'Download Complete Application'}
      </button>
      <div className="instructions-wrapper">
        <p>Installation Instructions:</p>
        <ol>
          <li>Download and extract the ZIP file</li>
          <li>Keep Power_Consumption_PSO.jar and lib folder together</li>
          <li>Open terminal/command prompt in the extracted folder</li>
          <li>Run: java -jar Power_Consumption_PSO.jar</li>
        </ol>
      </div>
    </div>
  );
  
  
};

export default DownloadButton;
