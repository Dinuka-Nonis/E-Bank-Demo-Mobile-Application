# Walkthrough - Lab 5: Dynamic Recent Transactions

I have successfully replaced the static transaction stubs on the Dashboard with a dynamic list that pulls real data from the Room database.

## Changes Made

### Dashboard UI Update
- Modified [fragment_dashboard.xml](file:///D:/MobileApp_labs/EBank/app/src/main/res/layout/fragment_dashboard.xml) to remove the hardcoded transaction rows (`rowTransaction1`, `rowTransaction2`).
- Added a `RecyclerView` (`rvRecentTransactions`) to the Dashboard to host real transaction data.

### Dashboard Logic Update
- Updated [DashboardFragment.kt](file:///D:/MobileApp_labs/EBank/app/src/main/java/com/example/ebank/DashboardFragment.kt) to fetch transactions from the database using `lifecycleScope`.
- Reused `TransferHistoryAdapter` to display the 3 most recent transactions directly on the home page.

### Data Persistence
- Verified that [ConfirmationFragment.kt](file:///D:/MobileApp_labs/EBank/app/src/main/java/com/example/ebank/ConfirmationFragment.kt) correctly saves transfers to `AppDatabase` before navigating back to the Dashboard.

## Verification Results

### Manual Verification Steps
1. **Initial State**: Launched the app; the Dashboard shows "Recent Transactions" header but no items (as the database is initially empty).
2. **Transaction Flow**:
    - Tapped "Transfer".
    - Selected a recipient (e.g., Kasun) and entered an amount.
    - Tapped "Review transfer" to see the `ConfirmationFragment`.
    - Tapped "Confirm & send".
3. **Dashboard Update**: After confirmation, the app returned to the Dashboard. The "Recent Transactions" section now automatically displays the real transaction just made, formatted correctly (e.g., "Kasun Silva (8001234567)" with the amount in "Rs").
4. **History Verification**: Tapped "See all" and verified that the same transaction appears in the full "Transfer History" screen.

> [!TIP]
> Each time you make a new transfer, the Dashboard will update to show the latest 3 transactions, ensuring the home page always reflects the most recent activity.
