import UIKit
import SharedCode

class ViewController: UIViewController {
    @IBOutlet weak var originSpinner: UIPickerView!
    @IBOutlet weak var destinationSpinner: UIPickerView!
    @IBOutlet weak var submitButton: UIButton!
    
    private var stations: [String] = [String]()
    
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
        
        setUpPickers()
    }
    
    @IBAction func stationSubmitButtonTapped(_ sender: Any) {
        let originStation = stations[originSpinner.selectedRow(inComponent: 0)]
        let destinationStation = stations[destinationSpinner.selectedRow(inComponent: 0)]
        presenter.onStationSubmitButtonPressed(originStation: originStation, destinationStation: destinationStation)
    }
}

extension ViewController: UIPickerViewDataSource, UIPickerViewDelegate {
    func setUpPickers() {
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
    func openUrl(url: String) {
        UIApplication.shared.open(URL(string: url)!)
        return
    }
    
    func setStationSubmitButtonText(text: String) {
        submitButton.setTitle(text, for: .normal)
    }
    
    func populateOriginAndDestinationSpinners(stations: Array<String>) {
        self.stations = stations
    }
}
