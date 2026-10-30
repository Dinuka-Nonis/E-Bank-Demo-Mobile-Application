# Implementation Plan - Lab 5: Dynamic Recent Transactions

This plan covers the transition from static stubs to dynamic data on the Dashboard screen, as requested. We will use the existing Room database and `TransferHistoryAdapter` to display real transactions on the home page.

## User Review Required

> [!IMPORTANT]
> The "Recent Transactions" section on the Dashboard will now show up to 3 most recent transfers from the database. If no transfers have been made, this section will be empty (or show a placeholder if we add one).

## Proposed Changes

### [Component] Dashboard

#### [MODIFY] [fragment_dashboard.xml](file:///D:/MobileApp_labs/EBank/app/src/main/res/layout/fragment_dashboard.xml)
- Remove static `LinearLayout` rows for `rowTransaction1` and `rowTransaction2`.
- Add a `androidx.recyclerview.widget.RecyclerView` with ID `rvRecentTransactions` below the "Recent Transactions" header.

#### [MODIFY] [DashboardFragment.kt](file:///D:/MobileApp_labs/EBank/app/src/main/java/com/example/ebank/DashboardFragment.kt)
- Initialize `rvRecentTransactions` in `onViewCreated`.
- Launch a lifecycle-aware coroutine to fetch transactions from `AppDatabase`.
- Limit the results to the top 3 and bind them using `TransferHistoryAdapter`.

### [Component] Confirmation

#### [MODIFY] [ConfirmationFragment.kt](file:///D:/MobileApp_labs/EBank/app/src/main/java/com/example/ebank/ConfirmationFragment.kt)
- Ensure the database insertion is completed or at least correctly handled before navigating. (The current implementation is mostly fine but I'll ensure it follows the lab's spirit).

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.

### Manual Verification
1. Launch the app.
2. Observe that the Dashboard's "Recent Transactions" section is empty (if DB is fresh).
3. Navigate to "Transfer", fill in details, and "Confirm".
4. Return to Dashboard and verify that the new transaction appears in the "Recent Transactions" list.
5. Navigate to "Transfer History" (via "See all") and verify the transaction is also there.
