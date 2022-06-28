import UIKit
import SharedCode

class ViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {
    @IBOutlet weak var originSpinner: UIPickerView!
    @IBOutlet weak var destinationSpinner: UIPickerView!
    @IBOutlet private var label: UILabel!
    
    private var stations: [String] = [String]()
    
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
        
        originSpinner.delegate = self
        originSpinner.dataSource = self
        
        destinationSpinner.delegate = self
        destinationSpinner.dataSource = self
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return stations.capacity
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return stations[row]
    }
    

}

extension ViewController: ApplicationContractView {
    func getDestinationStation() -> String {
        return ""
    }
    
    func getOriginStation() -> String {
        return ""
    }
    
    func openUrl(url: String) {
        return
    }
    
    func setStationSubmitButtonHandler() {
        return
    }
    
    func setStationSubmitButtonText(text: String) {
        return
    }
    
    func setLabel(text: String) {
        label.text = text
    }

    
    func populateOriginAndDestinationSpinners(stations: Array<String>) {
        self.stations = stations
    }
}
