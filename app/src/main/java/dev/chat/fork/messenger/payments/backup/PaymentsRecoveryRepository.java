package dev.chat.fork.messenger.payments.backup;

import androidx.annotation.NonNull;

import dev.chat.fork.messenger.keyvalue.SignalStore;
import dev.chat.fork.messenger.payments.Mnemonic;

public final class PaymentsRecoveryRepository {
  public @NonNull Mnemonic getMnemonic() {
    return SignalStore.payments().getPaymentsMnemonic();
  }
}
