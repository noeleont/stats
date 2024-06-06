import SwiftUI
import Charts
import Shared

struct ContentView: View {
    @ObservedObject var viewModel: ViewModel = ViewModel()
    
    var body: some View {
        NavigationStack {
            VStack {
                Picker("Period", selection: $viewModel.selectedPeriod) {
                    Text("Day").tag("day")
                    Text("Week").tag("week")
                    Text("Month").tag("month")
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding()
                Chart(viewModel.entries) { entry in
                    BarMark(
                        x: .value("Date", entry.period),
                        y: .value("Total Entries", entry.totalEntries)
                    )
                }
                .onChange(of: viewModel.selectedPeriod, initial: true) {
                    Task {
                        await viewModel.activate()
                    }
                }
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        viewModel.addEntry()
                    }) {
                        Image(systemName: "plus")
                    }
                }
            }
        }
    }
}


extension ContentView {
    class ViewModel: ObservableObject {
        @Published var entries = [TotalEntriesPerPeriod]()
        @Published var selectedPeriod: String = "day"
        let helper: KoinHelper = KoinHelper()
        private var currentTask: Task<Void, Never>?
        
        @MainActor
        func activate() async {
            currentTask?.cancel()
            
            let task = Task {
                for await entries in helper.entrySubscription(period: selectedPeriod) {
                    guard !Task.isCancelled else { return }
                    self.entries = entries
                }
            }
            
            currentTask = task
            
            await task.value
        }
        
        func addEntry() {
            Task.detached {
                try? await self.helper.addEntry()
            }
        }
    }
}

extension TotalEntriesPerPeriod: Identifiable {}
