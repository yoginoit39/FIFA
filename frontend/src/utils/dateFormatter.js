/**
 * Date formatting utilities
 */
import { format, parseISO, isValid } from 'date-fns';

/**
 * Format date to readable string
 * @param {string|Date} date - Date to format
 * @param {string} formatString - Format string (default: 'MMM dd, yyyy')
 * @returns {string} - Formatted date
 */
export const formatDate = (date, formatString = 'MMM dd, yyyy') => {
  if (!date) return '';

  try {
    const dateObj = typeof date === 'string' ? parseISO(date) : date;
    if (!isValid(dateObj)) return '';
    return format(dateObj, formatString);
  } catch (error) {
    console.error('Error formatting date:', error);
    return '';
  }
};

/**
 * Format time to readable string
 * @param {string} time - Time string (HH:mm format)
 * @returns {string} - Formatted time (h:mm a)
 */
export const formatTime = (time) => {
  if (!time) return '';

  try {
    // Parse time string (assuming HH:mm format)
    const [hours, minutes] = time.split(':');
    const date = new Date();
    date.setHours(parseInt(hours, 10));
    date.setMinutes(parseInt(minutes, 10));

    return format(date, 'h:mm a');
  } catch (error) {
    console.error('Error formatting time:', error);
    return time;
  }
};

/**
 * Format date and time together
 * @param {string|Date} date - Date
 * @param {string} time - Time
 * @returns {string} - Formatted date and time
 */
export const formatDateTime = (date, time) => {
  const formattedDate = formatDate(date, 'EEEE, MMM dd, yyyy');
  const formattedTime = formatTime(time);

  if (formattedDate && formattedTime) {
    return `${formattedDate} at ${formattedTime}`;
  }

  return formattedDate || formattedTime || '';
};

/**
 * Get relative time (e.g., "in 2 days", "yesterday")
 */
export const getRelativeTime = (date) => {
  if (!date) return '';

  try {
    const dateObj = typeof date === 'string' ? parseISO(date) : date;
    if (!isValid(dateObj)) return '';

    const now = new Date();
    const diffTime = dateObj - now;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < 0) {
      return 'Past';
    } else if (diffDays === 0) {
      return 'Today';
    } else if (diffDays === 1) {
      return 'Tomorrow';
    } else if (diffDays < 7) {
      return `In ${diffDays} days`;
    } else {
      return formatDate(dateObj);
    }
  } catch (error) {
    console.error('Error getting relative time:', error);
    return '';
  }
};
