import SwiftUI
import Shared

struct ContentView: View {
    @ObservedObject var viewModel: ViewModel = ViewModel()
    
    var body: some View {
        NavigationStack {
            List(viewModel.entries) { entry in
                Text(entry.date.description())
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
            .task {
                await viewModel.activate()
            }
        }
    }
}


extension ContentView {
    class ViewModel: ObservableObject {
        @Published var entries = [Entry]()
        let helper: KoinHelper = KoinHelper()
        
        @MainActor
        func activate() async {
            for await entries in helper.entrySubscription() {
                self.entries = entries
            }
        }
        
        func addEntry() {
            Task.detached {
                try? await self.helper.addEntry()
            }
        }
        
    }
}

extension Entry: Identifiable {}
