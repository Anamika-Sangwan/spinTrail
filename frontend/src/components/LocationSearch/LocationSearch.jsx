import { useState, useEffect, useRef } from 'react';
import { searchLocations } from '../../services/apiService';
import './LocationSearch.css';

function LocationSearch({ onLocationSelect }) {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const [selectedLabel, setSelectedLabel] = useState('');

  // Used to cancel previous search if user keeps typing
  const debounceTimer = useRef(null);
  // Used to close dropdown when clicking outside
  const wrapperRef = useRef(null);

  // ── Close dropdown on outside click ──────────────────────────────────────
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // ── Debounced search — waits 400ms after user stops typing ────────────────
  // Prevents hammering the Nominatim API on every keystroke
  useEffect(() => {
    if (query.trim().length < 3) {
      setSuggestions([]);
      setShowDropdown(false);
      return;
    }

    // Clear the previous timer
    clearTimeout(debounceTimer.current);

    // Set a new timer — only fires if user stops typing for 400ms
    debounceTimer.current = setTimeout(async () => {
      setIsLoading(true);
      setShowDropdown(true);
      try {
        const results = await searchLocations(query);
        setSuggestions(results);
      } catch (err) {
        console.error('Location search failed:', err);
        setSuggestions([]);
      } finally {
        setIsLoading(false);
      }
    }, 400);

    // Cleanup: cancel timer if component unmounts mid-wait
    return () => clearTimeout(debounceTimer.current);
  }, [query]);

  // ── User picks a suggestion ───────────────────────────────────────────────
  const handleSelect = (suggestion) => {
    // Show a clean short label in the input instead of the full display name
    const shortLabel = suggestion.displayName.split(',').slice(0, 2).join(',');
    setSelectedLabel(shortLabel);
    setQuery(shortLabel);
    setShowDropdown(false);
    setSuggestions([]);

    // Bubble the selected location up to App.js
    // onLocationSelect receives { lat, lng, label }
    onLocationSelect({
      lat: suggestion.latitude,
      lng: suggestion.longitude,
      label: suggestion.displayName,
    });
  };

  // ── Clear the search box ─────────────────────────────────────────────────
  const handleClear = () => {
    setQuery('');
    setSelectedLabel('');
    setSuggestions([]);
    setShowDropdown(false);
  };

  // ── Split display name into main + sub parts for the dropdown ─────────────
  // "Cubbon Park, Bangalore, Karnataka, India"
  // → main: "Cubbon Park"   sub: "Bangalore, Karnataka, India"
  const splitDisplayName = (displayName) => {
    const parts = displayName.split(',');
    return {
      main: parts[0].trim(),
      sub: parts.slice(1).join(',').trim(),
    };
  };

  return (
    <div className="location-search" ref={wrapperRef}>

      {/* ── Search Input ── */}
      <div className="search-input-wrapper">
        <span className="search-icon">📍</span>
        <input
          type="text"
          className="search-input"
          placeholder="Search start location..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => {
            if (suggestions.length > 0) setShowDropdown(true);
          }}
        />
        {query && (
          <button className="clear-btn" onClick={handleClear}>✕</button>
        )}
      </div>

      {/* ── Dropdown ── */}
      {showDropdown && (
        <div className="suggestions-dropdown">

          {/* Loading state */}
          {isLoading && (
            <div className="dropdown-state">
              <div className="loading-dots">
                <span>.</span><span>.</span><span>.</span>
              </div>
            </div>
          )}

          {/* No results */}
          {!isLoading && suggestions.length === 0 && (
            <div className="dropdown-state">No locations found</div>
          )}

          {/* Suggestion list */}
          {!isLoading && suggestions.map((suggestion, index) => {
            const { main, sub } = splitDisplayName(suggestion.displayName);
            return (
              <div
                key={index}
                className="suggestion-item"
                onClick={() => handleSelect(suggestion)}
              >
                <span className="suggestion-pin">📍</span>
                <div className="suggestion-text">
                  <span className="suggestion-main">{main}</span>
                  <span className="suggestion-sub">{sub}</span>
                </div>
              </div>
            );
          })}

        </div>
      )}
    </div>
  );
}

export default LocationSearch;