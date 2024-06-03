import SwiftUI
import Shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel
    
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
        }
    }
}


extension ContentView {
    @MainActor
    class ViewModel: ObservableObject {
        @Published var entries = [Entry]()
        let helper: KoinHelper = KoinHelper()
        
        init() {
            loadEntries()
        }
        
        func loadEntries() {
            Task {
                do {
                    self.entries = try await helper.getEntries()
                } catch {
                    print(error.localizedDescription)
                }
            }
        }
        
        func addEntry() {
            Task {
                do {
                    try await helper.addEntry()
                    self.entries = try await helper.getEntries()
                } catch {
                    print(error.localizedDescription)
                }
                
            }
        }
        
    }
}

extension Entry: Identifiable {}
