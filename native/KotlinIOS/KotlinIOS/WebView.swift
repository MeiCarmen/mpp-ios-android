//
//  WebView.swift
//  KotlinIOS
//
//  Created by Carmen Meinson on 08/07/2022.
//  Copyright © 2022 Evgeny Petrenko. All rights reserved.
//

import Foundation
import UIKit
import SharedCode
import WebKit

class WebView: UIViewController {
    @IBOutlet weak var webView: WKWebView!
    var url: URL?
   
    convenience init(urlString: String) {
        self.init()
        self.modalPresentationStyle = .fullScreen
        self.url = URL(string: urlString)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if (self.url != nil) {webView.load(URLRequest(url: self.url!))}
    }
    
    @IBAction func backButtonAction(_ sender: Any) {
        self.dismiss(animated: true)
    }
}
