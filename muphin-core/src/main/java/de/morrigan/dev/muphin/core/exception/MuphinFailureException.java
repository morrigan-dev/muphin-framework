package de.morrigan.dev.muphin.core.exception;

import java.util.Optional;

import org.slf4j.helpers.MessageFormatter;

public class MuphinFailureException extends Exception {

  private static final long serialVersionUID = 1L;

  public MuphinFailureException(String message, Object... params) {
    this(Optional.empty(), message, params);
  }

  public MuphinFailureException(Throwable cause, String message, Object... params) {
    this(Optional.ofNullable(cause), message, params);
  }

  private MuphinFailureException(Optional<Throwable> cause, String message,
      Object... params) {
    super(MessageFormatter.arrayFormat(message, params).getMessage(), cause.isPresent() ? cause.get() : null);
  }
}
