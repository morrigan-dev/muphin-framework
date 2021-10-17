package de.morrigan.dev.muphin.core.exception;

import java.util.Optional;

import org.slf4j.helpers.MessageFormatter;

/**
 * Used to catch various errors within the framework and combine them into a single exception class.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class MuphinFailureException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a new instance of this exception with a message that can be parameterized.
   *
   * @param message a message
   * @param params parameter for the message
   * @see MessageFormatter#arrayFormat(String, Object[])
   * @since 0.0.1
   */
  public MuphinFailureException(String message, Object... params) {
    this(Optional.empty(), message, params);
  }

  /**
   * Creates a new instance of this exception with a cause and a message that can be parameterized.
   *
   * @param cause a cause
   * @param message a message
   * @param params parameter for the message
   * @see MessageFormatter#arrayFormat(String, Object[])
   * @since 0.0.1
   */
  public MuphinFailureException(Throwable cause, String message, Object... params) {
    this(Optional.ofNullable(cause), message, params);
  }

  private MuphinFailureException(Optional<Throwable> cause, String message,
      Object... params) {
    super(MessageFormatter.arrayFormat(message, params).getMessage(), cause.isPresent() ? cause.get() : null);
  }
}
